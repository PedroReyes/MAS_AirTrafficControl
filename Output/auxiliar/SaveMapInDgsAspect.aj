package Output.auxiliar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.graphstream.graph.Node;

/**
 * En este aspecto añadiremos los metodos que son especificos de la aplicacion que estamos realizando. Por ejemplo, aqui añadiremos un metodo
 * tal como addAeropuerto() el cual es especifico de la aplicacion que realizamos dejando la clase Map.java con los metodos esenciales para su ejecucion.
 * @author pedro
 *
 */
public aspect SaveMapInDgsAspect {

	// Variables para el guardado del grafo en un fichero con formato .dgs
	private Scanner Map.scanner;

	/**
	 * Execute an action on the map
	 * 
	 * @param action
	 * @throws IOException
	 */
	public void Map.addActionToDgsFile(int step, String action) throws IOException {
		// Step
		String timeStep = "st" + step;

		Path path = Paths.get(dgsFilePath);

		scanner = new Scanner(new File(dgsFilePath));
		String content = scanner.useDelimiter("\\Z").next();
		// System.out.println(content);

		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			writer.write(content);
			if (!content.contains(timeStep)) {
				writer.write("\n" + timeStep + "\n");
			}
			writer.write("\n" + action + "\n");
		}
	}

	// Añadir un nodo
	pointcut addNode(Map map, int step, int posX, int posY, int posZ):
		this(map) &&
		call(org.graphstream.graph.Node MyExample.Map.addNode(int, int, int, int)) 
		&& args(step, posX, posY, posZ);

	after(Map map, int step, int posX, int posY, int posZ) returning:
		addNode(map, step, posX, posY, posZ){
		try {
			map.addActionToDgsFile(step, "an " + map.getNodeIdBasedOnMapPosition(
					String.valueOf(posX), 
					String.valueOf(posY), 
					String.valueOf(posZ)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Añadir atribudo a una arista
	pointcut addNodeAttribute(Map map, int step, org.graphstream.graph.Node node, String attribute, String values):
		this(map) &&
		call(void MyExample.Map.addAttribute(int, org.graphstream.graph.Node, String, String)) 
		&& args(step, node, attribute, values);


	after(Map map, int step, org.graphstream.graph.Node node, String attribute, String values) returning : 
		addNodeAttribute(map, step, node, attribute, values){
		try {
			map.addActionToDgsFile(step, "cn " + node.getId() + " " + attribute + "="+values);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Establecer la posición de un nodo
	pointcut setNodePosition(Map map, int step, Node node, int posX, int posY, int posZ):
		this(map) &&
		call(void MyExample.Map.setNodePosition(int, Node, int, int, int)) 
		&& args(step, node, posX, posY, posZ);

	after(Map map, int step, Node node, int posX, int posY, int posZ) returning:
		setNodePosition(map, step, node, posX, posY, posZ){
		try {
			map.addActionToDgsFile(step, "cn " + node.getId() + " x=" + posX + " y=" + posY + " z=" + posZ);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	// Añadir una arista
	pointcut addEdge(Map map, int step, String id, org.graphstream.graph.Node node1, org.graphstream.graph.Node node2):
		this(map) &&
		call(org.graphstream.graph.Edge MyExample.Map.addEdge(int, String, org.graphstream.graph.Node, org.graphstream.graph.Node)) 
		&& args(step, id, node1, node2);


	after(Map map, int step, String id, org.graphstream.graph.Node node1, org.graphstream.graph.Node node2) returning : 
		addEdge(map, step, id, node1, node2){
		// Añadimos la arista al fichero Dgs
		try {
			map.addActionToDgsFile(step, "ae " + id + " " + node1.getId() + " " + node2.getId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	// Añadir atribudo a una arista
	pointcut addEdgeAttribute(Map map, int step, org.graphstream.graph.Edge edge, String attribute, String values):
		this(map) &&
		call(void MyExample.Map.addAttribute(int, org.graphstream.graph.Edge, String, String)) 
		&& args(step, edge, attribute, values);


	after(Map map, int step, org.graphstream.graph.Edge edge, String attribute, String values) returning : 
		addEdgeAttribute(map, step, edge, attribute, values){
		try {
			map.addActionToDgsFile(step, "ce " + edge.getId() + " " + attribute + "="+values);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
