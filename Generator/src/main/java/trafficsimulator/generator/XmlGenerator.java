package trafficsimulator.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import trafficsimulator.generator.exceptions.UnknownSignalNumberException;
import trafficsimulator.shared.dto.Intersection;
import trafficsimulator.shared.dto.Lane;
import trafficsimulator.shared.dto.Phase;
import trafficsimulator.shared.dto.Sequence;
import trafficsimulator.shared.dto.SignalGroup;
import trafficsimulator.shared.dto.SignalProgram;
import trafficsimulator.shared.dto.SumoPhase;
import trafficsimulator.shared.helper.FileNames;

/**
 * @author z003ru0y
 * Helper class for the generator
 */
public class XmlGenerator
{  
  private static Logger logger = LoggerFactory.getLogger(XmlGenerator.class);
  
  private final FileNames fileNames;
  
  /**
   * generic constructor
   * @param fileNames object with user-defined file names
   */
  public XmlGenerator(FileNames fileNames)
  {
    this.fileNames = fileNames;
    logger.debug("XML Generator started");
  }

  /**
   * Sets attributes of a node and returns new element
   * 
   * @param doc xml document instance 
   * @param elemName name of a element to be created
   * @param attributes double array of attributes
   * @return new element instance
   */
  public Element createElement(Document doc, String elemName, String[][] attributes)
  {
    Element element = doc.createElement(elemName);
    for (int i = 0; i < attributes.length; i++)
    {
      element.setAttribute(attributes[i][0], attributes[i][1]);
    }
    return element;
  }

  /**
   * Method prepares the XML Document with predefined root
   * 
   * @param rootName root element 
   * @return XML document instance 
   * @throws ParserConfigurationException when the document builder fails 
   */
  public Document prepareDocument(String rootName) throws ParserConfigurationException
  {
    logger.debug("Preparing document for {}", rootName);
    
    Document doc = null;
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    // root elements
    doc = docBuilder.newDocument();

    Element rootElement = doc.createElement(rootName);
    doc.appendChild(rootElement);
    Attr attr = doc.createAttribute("xmlns:xsi");
    attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
    rootElement.setAttributeNode(attr);
    return doc;
  }

  /**
   * @param doc
   *          XML Document for saving
   * @param filename filename
   * @param path path to the configuration files
   * @throws TransformerException when the transformer fails
   */
  public void finalizeDocument(Document doc, String filename, String path)
      throws TransformerException
  {
    logger.debug("Writing to file: {}", filename);
    
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(new File(path + filename));
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    transformer.transform(source, result);
  }

  /**
   * Static method which just generates network configuration file
   * based on static data
   * Network configuration file is XML file which defines SUMO network for simulation
   * It is combination of node xml file, edge xml file and connection xml file
   * @param path path to the configuration files
   * 
   * @throws ParserConfigurationException when the document builder fails 
   * @throws TransformerException when the transformer fails
   */
  public void generateNetCfgFile(String path) throws ParserConfigurationException,
   TransformerException
  {

    Document doc = prepareDocument("configuration");
    Element rootElement = doc.getDocumentElement();
    Element input = doc.createElement("input");
    rootElement.appendChild(input);

    Element nodeFiles = doc.createElement("node-files");
    nodeFiles.setAttribute("value", fileNames.getNodesFileName());
    input.appendChild(nodeFiles);

    Element edgeFiles = doc.createElement("edge-files");
    edgeFiles.setAttribute("value", fileNames.getEdgesFileName());
    input.appendChild(edgeFiles);

    Element connectionFiles = doc.createElement("connection-files");
    connectionFiles.setAttribute("value", fileNames.getConnectionsFileName());
    input.appendChild(connectionFiles);

    Element output = doc.createElement("output");
    rootElement.appendChild(output);

    Element outputFile = doc.createElement("output-file");
    outputFile.setAttribute("value", fileNames.getNetworkFileName());
    output.appendChild(outputFile);

    Element processing = doc.createElement("processing");
    rootElement.appendChild(processing);

    Element speed = doc.createElement("speed-in-kmh");
    speed.setAttribute("value", "true");
    processing.appendChild(speed);

    Element report = doc.createElement("report");
    rootElement.appendChild(report);

    Element xmlValidation = doc.createElement("xml-validation");
    xmlValidation.setAttribute("value", "never");
    report.appendChild(xmlValidation);

    finalizeDocument(doc, fileNames.getNetworkConfFileName(), path);
  }

  /**
   * Method generates common SUMO configuration based on files which should be already created
   * @param path path to the configuration files
   * 
   * @throws ParserConfigurationException when the document builder fails 
   * @throws TransformerException when the transformer fails
   */
  public void generateSumoCfgFile(String path) 
      throws ParserConfigurationException, TransformerException
  {
    Document doc = prepareDocument("configuration");
    Element rootElement = doc.getDocumentElement();
    Element input = doc.createElement("input");
    rootElement.appendChild(input);

    Element netFiles = doc.createElement("net-file");
    netFiles.setAttribute("value", fileNames.getNetworkFileName());
    input.appendChild(netFiles);

    Element routeFiles = doc.createElement("route-files");
    routeFiles.setAttribute("value", fileNames.getRoutesFileName());
    input.appendChild(routeFiles);

    Element addFiles = doc.createElement("additional-files");
    addFiles.setAttribute("value", fileNames.getTlsFileName() + " "
      + fileNames.getDetFileName());
    input.appendChild(addFiles);

    finalizeDocument(doc, fileNames.getSumoConfigurationFileName(), path);
  }

  /**
   * Transforms sequences from original configuration to SUMO-readable phase strings
   * 
   * @param sp signal program to parse
   * @param intersection intersection 
   * @return list of sumo-readable phases
   * @throws UnknownSignalNumberException when the signal number from configuration is unknown
   */
  public List<SumoPhase> transformSequencesToSumoPhases(SignalProgram sp, Intersection intersection)
    throws UnknownSignalNumberException
  {
    List<Sequence> sequenceList = new ArrayList<>();
    for (SignalGroup sg : intersection.getAllSignalGroups())
    {
      int laneDirCnt = 0;
      for (Lane lane : sg.getLanes())
      {
        laneDirCnt += lane.getDirections().getConnectionsCount();
      }
      
      int iterationCnt = sg.getDirections().getConnectionsCount() > intersection.getLegs().size()
        ? intersection.getLegs().size() - 1
        : laneDirCnt;
        

      for (int i = 0; i < iterationCnt; i++)
      {
        sequenceList.add(sg.getSequenceBySPId(sp.getId()));
      }

    }
    List<SumoPhase> sumoPhaseList = new ArrayList<>();

    // initial
    int i = 1;
    int newI = 1;
    String lastState = gatherStatesAtTime(0, sequenceList);
    int lastI = 0;
    boolean change = false;
    Phase currentPhase = null;
    int id = 0;

    for (i = newI; i < sp.getDuration(); i++)
    {
      for (Sequence sequence : sequenceList)
      {
        if (sequence.getSignalSeq()[i] != sequence.getSignalSeq()[i - 1])
        {
          change = true;
          currentPhase = sequence.getPhaseSeq()[i - 1];
        }
      }
      if (change)
      {
        newI = i;
        sumoPhaseList.add(new SumoPhase(id, i - lastI, lastState, currentPhase));
        id++;
        lastState = gatherStatesAtTime(i, sequenceList);
        lastI = newI;
        change = false;
      }
    }
    sp.setSumoPhases(sumoPhaseList);
    return sumoPhaseList;
  }


  /**
   * Gathers signal information from given sequences at given time
   * 
   * @param time time when to gather states
   * @param sequenceList list of sequences to read from
   * @return "GGggRrYy.." kind of SUMO-readable string
   * @throws UnknownSignalNumberException when the signal number from configuration is unknown
   */
  private String gatherStatesAtTime(int time, List<Sequence> sequenceList) throws UnknownSignalNumberException
  {
    /**
     * not available in public version
     */

    return "";
  }

}
