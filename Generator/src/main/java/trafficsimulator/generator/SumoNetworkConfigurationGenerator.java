package trafficsimulator.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import trafficsimulator.shared.dto.Situation;
import trafficsimulator.generator.exceptions.SumoNetworkGenerationException;
import trafficsimulator.generator.exceptions.UnknownSignalNumberException;
import trafficsimulator.shared.dto.Detector;
import trafficsimulator.shared.dto.GeneratedRoute;
import trafficsimulator.shared.dto.Intersection;
import trafficsimulator.shared.dto.IntersectionConnection;
import trafficsimulator.shared.dto.Lane;
import trafficsimulator.shared.dto.Leg;
import trafficsimulator.shared.dto.Flow;
import trafficsimulator.shared.dto.SignalGroup;
import trafficsimulator.shared.dto.SignalProgram;
import trafficsimulator.shared.dto.SumoPhase;
import trafficsimulator.shared.dto.VehicleType;
import trafficsimulator.shared.helper.FileNames;

/**
 * @author z003ru0y
 * Generates XML configuration files for SUMO
 */
public class SumoNetworkConfigurationGenerator
{
  private static Logger logger = LoggerFactory.getLogger(SumoNetworkConfigurationGenerator.class);
  
  
  @Autowired
  XmlGenerator xmlGenerator; 
  
  private final FileNames fileNames;
  
  private final String configurationPath;
  
  /**
   * @param confPath path to the configuration files
   * @param fileNames object with file names
   */
  public SumoNetworkConfigurationGenerator(String confPath, FileNames fileNames)
  {
    this.configurationPath = confPath;
    this.fileNames = fileNames;
    logger.info("Sumo configuration path: {}", this.configurationPath);
    logger.debug("Sumo Network Configuration Generator started");
  }

  /**
   * @param configuration infrastructure layoput to generate into files
   * @throws SumoNetworkGenerationException when the generation fails
   */
  public void generateConfiguration(Situation configuration)
    throws SumoNetworkGenerationException
  {
    logger.info("Generating SUMO cfg files");
    
    List<Intersection> intersections = configuration.getIntersectionList();

    try
    {
      generateNodeFile(intersections, configurationPath);
      generateEdgeFile(intersections, configuration.getIntersectionConnections(), configurationPath);
      generateConnectionFile(intersections, configuration.getIntersectionConnections(), configurationPath);
      xmlGenerator.generateNetCfgFile(configurationPath);
      generateNetFile(configurationPath);

      generateFlowsFile(configuration.getRoutes(), configurationPath);
      generateRoutesFile(configurationPath);
      configuration.setGeneratedRoutes(transformRoutesFile(configuration.getVehicleTypes(), configurationPath));
      generateTrafficLightFile(intersections, configurationPath);
      generateDetectorsFile(intersections, configurationPath);
      xmlGenerator.generateSumoCfgFile(configurationPath);
    }
    catch (
      ParserConfigurationException
      | TransformerFactoryConfigurationError
      | TransformerException e)
    {
      throw new SumoNetworkGenerationException(
          "Sumo configuration generator failed: javax.xml... :" + e.getMessage());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new SumoNetworkGenerationException(
          "Sumo configuration generator failed for unknown reason: javax.xml... :" + e.getMessage());
    }
  }

  private void generateNodeFile(List<Intersection> intersections, String confPath) 
  throws ParserConfigurationException, TransformerConfigurationException,
    TransformerFactoryConfigurationError, TransformerException
  {

    Document doc = xmlGenerator.prepareDocument("nodes");
    Element rootElement = doc.getDocumentElement();

    for (Intersection intersection : intersections)
    {
      // create intersection node
      String attributesPart0[][] =
      {
          { "id",
              Integer.toString(intersection.getId()) },
          { "x",
              Double.toString(intersection.getCoordinates().getX()) },
          { "y",
              Double.toString(intersection.getCoordinates().getY()) },
          { "type",
              "traffic_light" } };
      Element node = xmlGenerator.createElement(doc, "node", attributesPart0);
      rootElement.appendChild(node);

      for (Leg leg : intersection.getLegs())
      {
        // create leg node part 1 - far away
        String attributesPart1[][] =
        {
            { "id",
                leg.getId() },
            { "x",
                Double.toString(leg.getCoordinates().getX()) },
            { "y",
                Double.toString(leg.getCoordinates().getY()) },
            { "type",
                "priority" } };
        node = xmlGenerator.createElement(doc, "node", attributesPart1);
        rootElement.appendChild(node);

      }
    }

    xmlGenerator.finalizeDocument(doc, fileNames.getNodesFileName(), confPath);
  }

  private void generateEdgeFile(
		  List<Intersection> intersections, List<IntersectionConnection> connections, String confPath)
  throws ParserConfigurationException, TransformerConfigurationException,
    TransformerFactoryConfigurationError, TransformerException
  {
    Document doc = xmlGenerator.prepareDocument("edges");
    Element rootElement = doc.getDocumentElement();

    for (Intersection intersection : intersections)
    {

      String intersectionId = Integer.toString(intersection.getId());

      for (Leg leg : intersection.getLegs())
      {
        // Part 2 -
        String attributesPart2[][] =
        {
            { "id",
                leg.getEdgeInConnectionName() },
            { "from",
                /* "m" + */leg.getId() },
            { "to",
                intersectionId },
            { "priority",
                "1" },
            { "numLanes",
                Integer.toString((leg.getAllLanes().size())) },
            { "speed",
                "50" },
            { "spreadType",
                "center" } };
        Element edge = xmlGenerator.createElement(doc, "edge", attributesPart2);
        rootElement.appendChild(edge);

        // Part 3 -
        String attributesPart3[][] =
        {
            { "id",
                leg.getEdgeOutConnectionName() },
            { "from",
                intersectionId },
            { "to",
                leg.getId() },
            { "priority",
                "1" },
            { "numLanes",
                "1" },
            { "speed",
                "60" },
            { "spreadType",
                "center" } };
        edge = xmlGenerator.createElement(doc, "edge", attributesPart3);
        rootElement.appendChild(edge);
      }
    }

    for (IntersectionConnection connection : connections)
    {
      String attributesPart1[][] =
      {
          { "id",
              connection.getId() },
          { "from",
              connection.getLegI1().getId() },
          { "to",
              connection.getLegI2().getId() },
          { "priority",
              "1" },
          { "numLanes",
              "1" },
          { "speed",
              "40" },
          { "spreadType",
              "center" } };
      Element edge = xmlGenerator.createElement(doc, "edge", attributesPart1);
      rootElement.appendChild(edge);

      String attributesPart2[][] =
      {
          { "id",
              connection.getIdBack() },
          { "from",
              connection.getLegI2().getId() },
          { "to",
              connection.getLegI1().getId() },
          { "priority",
              "1" },
          { "numLanes",
              "1" },
          { "speed",
              "40" },
          { "spreadType",
              "center" } };
      edge = xmlGenerator.createElement(doc, "edge", attributesPart2);
      rootElement.appendChild(edge);

    }

    xmlGenerator.finalizeDocument(doc, fileNames.getEdgesFileName(), confPath);
  }

  private void generateConnectionFile(
		  List<Intersection> intersections, List<IntersectionConnection> connections, String confPath) 
  throws ParserConfigurationException, TransformerConfigurationException,
    TransformerFactoryConfigurationError, TransformerException
  {
    Document doc = xmlGenerator.prepareDocument("connections");
    Element rootElement = doc.getDocumentElement();
    int j = 0;
    for (Intersection intersection : intersections)
    {
      for (Leg leg : intersection.getLegs())
      {
        for (Lane lane : leg.getAllLanes())
        {
          for (Leg connectionLane : lane.getOutputLegs())
          {
            String[][] attributes =
            {
                { "from",
                    leg.getEdgeInConnectionName() },
                { "to",
                    connectionLane.getEdgeOutConnectionName() },
                { "fromLane",
                    Integer.toString(lane.getId()) },
                { "toLane",
                    "0" },
                { "tl",
                    Integer.toString(intersection.getId()) },
                { "linkIndex",
                    Integer.toString(j) } };
            Element connection = xmlGenerator.createElement(doc, "connection", attributes);
            // XXX hardcoded 0 id for exit lane
            rootElement.appendChild(connection);
            j++;
          }
        }
        String[][] attributes =
        {
            { "from",
                leg.getEdgeInConnectionName() },
            { "to",
                leg.getEdgeOutConnectionName() } };
        Element delete = xmlGenerator.createElement(doc, "delete", attributes);
        rootElement.appendChild(delete);
        
        String[][] attributesBack =
        {
            { "from",
                leg.getEdgeOutConnectionName() },
            { "to",
                leg.getEdgeInConnectionName() } };
        delete = xmlGenerator.createElement(doc, "delete", attributesBack);
        rootElement.appendChild(delete);
      }

    }
    
    for (IntersectionConnection connection : connections)
    {
      String[][] attributes =
      {
          { "from",
              connection.getId() },
          { "to",
                connection.getIdBack() } };
      Element delete = xmlGenerator.createElement(doc, "delete", attributes);
      rootElement.appendChild(delete);
      
      String[][] attributesBack =
      {
          { "from",
            connection.getIdBack() },
          { "to",
                connection.getId()  } };
      delete = xmlGenerator.createElement(doc, "delete", attributesBack);
      rootElement.appendChild(delete);
    }
    
    xmlGenerator.finalizeDocument(doc, fileNames.getConnectionsFileName(), confPath);
  }

  private void generateNetFile(String confPath) throws IOException, InterruptedException, SumoNetworkGenerationException
  {
    logger.debug("Using 'netconvert' to generate net file");

    String outputFilePath = confPath + fileNames.getNetworkFileName();
    Process netconvert =
      Runtime.getRuntime().exec("cmd /C start /wait netconvert "+ confPath + fileNames.getNetworkConfFileName());
    int exitValue = netconvert.waitFor();

    if (exitValue != 0)
    {
      throw new SumoNetworkGenerationException("'netconvert' failed with return value: " + exitValue);
    }

    logger.debug("'netconvert' finished successfuly, generated file: {}", outputFilePath);

  }

  private void generateFlowsFile(List<Flow> flows, String confPath) throws TransformerConfigurationException, TransformerFactoryConfigurationError,
    TransformerException, ParserConfigurationException
  {
    Document doc = xmlGenerator.prepareDocument("flowdefs");
    Element rootElement = doc.getDocumentElement();

    for (Flow route : flows)
    {
      String[][] attributes =
      {
          { "id",
              route.getId() },
          { "begin",
              "0" },
          { "end",
              "1" },
          { "from",
              route.getFrom().getEdgeInConnectionName() },
          { "to",
              route.getTo().getEdgeOutConnectionName() },
          { "probability",
              "1" } };
      Element flow = xmlGenerator.createElement(doc, "flow", attributes);
      rootElement.appendChild(flow);
    }

    xmlGenerator.finalizeDocument(doc, fileNames.getFlowsFileName(), confPath);
  }

  private void generateRoutesFile(String confPath) throws IOException, InterruptedException, SumoNetworkGenerationException
  {
    logger.debug("Using 'duarouter' to generate temporary routes file from flows file");
    
    String outputFilePath = confPath + fileNames.getRoutesTempFileName();
    Process duarouter = Runtime.getRuntime().exec("cmd /C start /wait duarouter -n " +confPath + fileNames.getNetworkFileName()
      + " -r " + confPath + fileNames.getFlowsFileName()
      + " -o " + outputFilePath);
    int exitValue = duarouter.waitFor();

    if (exitValue != 0)
    {
      throw new SumoNetworkGenerationException("'duarouter' failed with return value: " + exitValue);
    }

    logger.debug("'duarouter' finished successfuly, generated file: {}", outputFilePath);
  }

  private List<GeneratedRoute> transformRoutesFile(List<VehicleType> vehTypes, String confPath) throws ParserConfigurationException, SAXException, IOException,
    TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException
  {

    File fXmlFile = new File(confPath + fileNames.getRoutesTempFileName());

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document parsedRoutes = dBuilder.parse(fXmlFile);

    List<GeneratedRoute> routes = new ArrayList<>();

    NodeList nList = parsedRoutes.getElementsByTagName("vehicle");

    for (int temp = 0; temp < nList.getLength(); temp++)
    {

      Node nNode = nList.item(temp);

      if (nNode.getNodeType() == Node.ELEMENT_NODE)
      {

        Element eElement = (Element)nNode;
        String id = eElement.getAttribute("id");
        id = id.substring(0, id.indexOf("."));
        NodeList routeList = eElement.getElementsByTagName("route");
        if (routeList.getLength() > 0)
        {
          Element route = (Element)routeList.item(0);
          routes.add(new GeneratedRoute(route.getAttribute("edges"), id));
        }
        logger.trace("Creating route: {}", id);
      }
    }

    // fXmlFile.delete();

    // TODO delete alt files

    Document newRoutes = xmlGenerator.prepareDocument("routes");
    Element rootElement = newRoutes.getDocumentElement();

    for (VehicleType vType : vehTypes)
    {
      String[][] attributes =
      {
          { "id",
              vType.getName() },
          { "accel",
              vType.getAccel() + "" },
          { "decel",
              vType.getDecel() + "" },
          { "length",
              vType.getLength() + "" } };
      Element vtype = xmlGenerator.createElement(newRoutes, "vtype", attributes);
      rootElement.appendChild(vtype);
    }

    for (GeneratedRoute route : routes)
    {
      String[][] attributes =
      {
          { "id",
            route.getEdges() },
          { "edges",
            route.getId()  } };
      Element newRoute = xmlGenerator.createElement(newRoutes, "route", attributes);
      rootElement.appendChild(newRoute);
    }
    xmlGenerator.finalizeDocument(newRoutes, fileNames.getRoutesFileName(), confPath);

    return routes;
  }

  private void generateTrafficLightFile(List<Intersection> intersections, String confPath) throws ParserConfigurationException, TransformerConfigurationException,
    TransformerFactoryConfigurationError, TransformerException, UnknownSignalNumberException
  {
    Document doc = xmlGenerator.prepareDocument("additional");
    Element rootElement = doc.getDocumentElement();

    for (Intersection intersection : intersections)
    {
      for (SignalProgram sp : intersection.getSignalPrograms())
      {
        String[][] attributesTL =
        {
            { "id",
                Integer.toString(intersection.getId()) },
            { "programID",
                sp.getProgramId() },
            { "offset",
                Integer.toString(sp.getActivationOffset()) },
            { "type",
                "static" } };
        Element tlLogic = xmlGenerator.createElement(doc, "tlLogic", attributesTL);
        rootElement.appendChild(tlLogic);

        List<SumoPhase> sumoPhaseList = xmlGenerator.transformSequencesToSumoPhases(sp, intersection);

        for (SumoPhase sumoPhase : sumoPhaseList)
        {
          String[][] attributesPhase =
          {
              { "duration",
                  Integer.toString(sumoPhase.getDuration()) },
              { "state",
                  sumoPhase.getState() } };
          Element phase = xmlGenerator.createElement(doc, "phase", attributesPhase);
          tlLogic.appendChild(phase);
        }
      }
    }

    xmlGenerator.finalizeDocument(doc, fileNames.getTlsFileName(), confPath);
  }

  private void generateDetectorsFile(List<Intersection> intersections, String confPath) throws ParserConfigurationException, TransformerConfigurationException,
    TransformerFactoryConfigurationError, TransformerException
  {
    Document doc = xmlGenerator.prepareDocument("additional");
    Element rootElement = doc.getDocumentElement();

    for (Intersection intersection : intersections)
    {
      for (SignalGroup sg : intersection.getAllSignalGroups())
      {
        List<Lane> lanes = sg.getLanes();
        for (Detector det : sg.getDetectors())
        {
          for (Lane lane : lanes)
          {
            String laneDetId = det.getId() + "_"
              + lane.getId();
            det.addLaneUsed(lane);
            String[][] attributes =
            {
                { "id",
                    laneDetId },
                { "lane",
                    lane.getLaneNetId() },
                { "pos",
                    "20" },
                { "freq",
                    "900" },
                { "file",
                    det.getId() + ".xml" } };
            Element singleLoop = xmlGenerator.createElement(doc, "inductionLoop", attributes);
            rootElement.appendChild(singleLoop);
          }
        }
      }
    }

    xmlGenerator.finalizeDocument(doc, fileNames.getDetFileName(), confPath);
  }
}
