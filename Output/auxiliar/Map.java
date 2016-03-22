package Output.auxiliar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import Auxiliar.Vector;

public class Map {

	// Constantes
	final int nRow; // para la creacion del mapa
	final int nCol;

	final String nameDgsFile; // para poder ejecutar una simulacion anterior
	final String dgsFilePath;

	final String stylesheetPath; // para establecer un estilo css inicial al
									// mapa
	final String initialStylesheetName;

	// El grafo con el que tratamos durante la simulacion
	Graph graph;

	// La clase encargada de manejar con mayor facilidad los sprites en nuestro
	// grafo
	SpriteManager spriteManager;

	/**
	 * Constructor
	 * 
	 * @param nRow
	 * @param nCol
	 * @param nameDgsFile
	 * @throws IOException
	 */
	public Map(int nRow, int nCol, String dgsFilePath, String nameDgsFile, String stylesheetPath,
			String initialStylesheetName) throws IOException {
		// Establecemos las variables globales
		this.nRow = nCol; // filas del mapa
		this.nCol = nRow; // columnas del mapa
		this.nameDgsFile = nameDgsFile;
		// donde se almacenara el archivo dgs
		// System.getProperty("user.dir") + "/FicherosDgs/"
		this.dgsFilePath = dgsFilePath + (nameDgsFile.contains(".dgs") ? nameDgsFile : (nameDgsFile + ".dgs"));
		// donde se almacena el estilo inicial del mapa
		// System.getProperty("user.dir")
		this.stylesheetPath = stylesheetPath;
		this.initialStylesheetName = initialStylesheetName;

		// Creamos el directorio dodne se almacenan los estilos
		if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
			// Mac OS X
			(new File(stylesheetPath.substring(0, stylesheetPath.lastIndexOf('/')))).mkdirs();
		} else {
			// WINDOWS
			(new File(stylesheetPath.substring(0, stylesheetPath.lastIndexOf('\\')))).mkdirs();
		}
	}

	/**
	 * Una vez se llama a este metodo el mapa comienza a funcionar y podemos
	 * añadir acciones sobre el utilizando el metodo executeActionOnMap(String
	 * action)
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void executeSimulation(String dgsFilePath, String nameDgsFile, long speedness, boolean autoLayout)
			throws IOException, InterruptedException {
		Graph g = new DefaultGraph("g");
		spriteManager = new SpriteManager(g);
		String directory = dgsFilePath;
		// System.getProperty("user.dir") + "/FicherosDgs/";
		String file = (nameDgsFile.contains(".dgs") ? nameDgsFile : (nameDgsFile + ".dgs"));
		System.out.println("Executing simulation" + directory + file);
		FileSource fs = FileSourceFactory.sourceFor(directory + file);

		// We set the initial stylesheet
		// g.addAttribute("ui.stylesheet", "url('file://" + styleDirectory +
		// LeHavre_stylesheet.css + "')");

		// Establecemos el estilo inicial del grafo
		// Stylesheet
		g.addAttribute("ui.stylesheet", "url('file://" + this.stylesheetPath + this.initialStylesheetName + "')");

		// Mostramos el grafo paso a paso
		g.display(autoLayout);
		fs.addSink(g);

		try {
			fs.begin(directory + file);
			while (fs.nextStep()) {
				// Optionally some code here ...
				Thread.sleep(speedness);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			fs.end();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fs.removeSink(g);
		}
	}

	/**
	 * Crea un mapa con las dimensiones proporcionadas
	 * 
	 * @param nRow2
	 * @param nCol2
	 */
	public void createMap() {
		// TODO Auto-generated method stub
		// Creamos el fichero en caso de que no exista
		createDgsFile(this.nameDgsFile);

		// Creamos el grafo
		this.graph = new SingleGraph("Map " + nRow + " x " + nCol);// this.dgsFilePath);

		// Creamos el manejador de sprites
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		this.spriteManager = new SpriteManager(graph);

		// Creamos los nodos
		for (int row = 1; row <= this.nRow; row++) {
			for (int col = 1; col <= this.nCol; col++) {
				int realRowInMatrixWay = this.nRow - row + 1;
				int realColInMatrixWay = this.nCol - col + 1;
				int zPosition = 0;
				Node node = this.addNode(0, col, realRowInMatrixWay, zPosition);
				this.setNodePosition(0, node, realRowInMatrixWay, realColInMatrixWay, zPosition);
				this.addAttribute(0, node, "ui.label", node.getId());
			}
		}

		// Creamos las aristas
		for (int row = 1; row <= this.nRow; row++) {
			for (int col = 1; col <= this.nCol; col++) {
				int realRowInMatrixWay = this.nRow - row + 1;
				int realColInMatrixWay = col;

				// ARISTAS CON LOS NODOS ALREDEDOR
				for (int i = -1; i <= 1; i++) {
					for (int j = -1; j <= 1; j++) {
						int row2 = (realRowInMatrixWay + i);
						int col2 = (realColInMatrixWay + j);
						// Si se cumplen las restricciones necesarias para que
						// la creación de una arista sea valida, se crea dicha
						// arista.
						if ((row2 >= 1 && row2 <= this.nRow) && (col2 >= 1 && col2 <= this.nCol)
								&& !((realRowInMatrixWay == row2) && (realColInMatrixWay == col2))) {
							// We set the nodes
							Node node1 = graph.getNode(getNodeIdBasedOnMapPosition(String.valueOf(col),
									String.valueOf(realRowInMatrixWay), "0"));
							Node node2 = graph.getNode(
									getNodeIdBasedOnMapPosition(String.valueOf(col2), String.valueOf(row2), "0"));
							// Conseguimos los posibles ids de la arista formado
							// por los dos nodos
							String id1 = node1.getId();
							String id2 = node2.getId();
							String id = getEdgeIdBasedOnMapPosition(id1, id2);
							String idInverse = getEdgeIdBasedOnMapPosition(id2, id1);

							// Si la arista no existe, se crea la arista (si
							// existe y se crea daría un error debido a que son
							// aristas bidireccionales las que estamos creando)
							if ((graph.getEdge(id) == null && graph.getEdge(idInverse) == null)) {
								// Creamos la arista despues de todas las
								// comprobaciones
								Edge edge = this.addEdge(0, id, node1, node2);

								// Pintamos los bordes
								if ((realRowInMatrixWay == row2
										&& (realRowInMatrixWay - 1 == 0 || realRowInMatrixWay + 1 > this.nRow))
										|| (realColInMatrixWay == col2 && (realColInMatrixWay - 1 == 0
												|| realColInMatrixWay + 1 > this.nCol))) {
									this.addAttribute(0, edge, "ui.class", "bordes");
								} else {
									this.addAttribute(0, edge, "ui.class", "invisibles");
								}
							}
						}
					}
				}
			}
		}

		// Establecemos el estilo inicial del grafo
		// Stylesheet
		graph.addAttribute("ui.stylesheet", "url('file://" + this.stylesheetPath + this.initialStylesheetName + "')");

		// Mostramos el grafo (ponemos false para que las posiciones de los
		// objetos sean las que nosotros establezcamos)
		this.graph.display(false);
	}

	/**
	 * Crea el fichero DGS que sera en ultima instancia la fuente de las
	 * acciones que se veran reflejadas sobre el grafo.
	 * 
	 * @param nameDgsFile
	 */
	private void createDgsFile(String nameDgsFile) {
		try {
			// Se crea el fichero, fuente de los eventos que se producen en el
			// mapa
			if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
				// Mac OS X
				(new File(dgsFilePath.substring(0, dgsFilePath.lastIndexOf('/')))).mkdirs();
			} else {
				// WINDOWS
				(new File(dgsFilePath.substring(0, dgsFilePath.lastIndexOf('\\')))).mkdirs();
			}
			File file = new File(dgsFilePath);

			if (file.createNewFile()) {
				System.out.println("File is created!");
			} else {
				System.out.println("File already exists.");
			}

			// Se inicializa el fichero
			Path path = Paths.get(dgsFilePath);
			try (BufferedWriter writer = Files.newBufferedWriter(path)) {
				writer.write("DGS004");
				writer.newLine();
				writer.write("'null' 0 0");
				writer.newLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Añade una arista al grafo
	 * 
	 * @param step
	 * @param id
	 * @param n1
	 * @param n2
	 * @return
	 */
	private Edge addEdge(int step, String id, Node n1, Node n2) {
		return this.graph.addEdge(id, n1, n2);
	}

	/**
	 * Añade un atributo a la arista del grafo
	 * 
	 * @param edge
	 * @param string
	 * @param string2
	 */
	public void addAttribute(int step, Edge edge, String attribute, String values) {
		// TODO Auto-generated method stub
		edge.addAttribute(attribute, values);
	}

	/**
	 * Añade un atributo a la arista del grafo
	 * 
	 * @param edge
	 * @param string
	 * @param string2
	 */
	private void addAttribute(int step, Node node, String attribute, String values) {
		// TODO Auto-generated method stub
		node.addAttribute(attribute, values);
	}

	/**
	 * Añade un nodo al grafo
	 * 
	 * @param posX
	 * @param posY
	 * @return
	 */
	private Node addNode(int step, int posX, int posY, int posZ) {
		String id = getNodeIdBasedOnMapPosition(String.valueOf(posX), String.valueOf(posY), String.valueOf(posZ));
		Node node = this.graph.addNode(id);
		return node;
	}

	/**
	 * Cambia el atributo de un nodo del grafo
	 * 
	 * @param node
	 * @param row
	 * @param col
	 */
	private void setNodePosition(int step, Node node, int posX, int posY, int posZ) {
		node.setAttribute("xyz", posX, posY, posZ);
	}

	/**
	 * Los nodos tendrán un id que vendrá dada siempre por su posición en el
	 * mapa
	 * 
	 * @param posX
	 * @return
	 */
	public String getNodeIdBasedOnMapPosition(String posX, String posY, String posZ) {
		// fila, columna, altura
		return "f" + posX + "c" + posY + "a" + posZ;
	}

	/**
	 * Las aristas tendrán un id que vendrá dada siempre por la union de los ids
	 * de los nodos del mapa
	 * 
	 * @param posX
	 * @return
	 */
	public String getEdgeIdBasedOnMapPosition(String idNode1, String idNode2) {
		// fila, columna, altura
		return "N1" + idNode1 + "N2" + idNode2;
	}

	/**
	 * Añade un nuevo objeto al mapa, concretamente adjunta dich objeto a un
	 * nodo del mapa
	 * 
	 * @param objectId
	 */
	public void addNewObjectTo(int step, String objectId, int posX, int posY, int posZ, String spriteClass) {
		// Crea el sprite/objeto
		Sprite sprite = this.spriteManager.addSprite(objectId);
		String attribute = "ui.class";
		String values = spriteClass;

		// Añadimos el sprite
		sprite.addAttribute(attribute, values);

		// Conseguimos el nodo en el qeu se va a situar el objeto
		String nodeId = getNodeIdBasedOnMapPosition(String.valueOf(posX), String.valueOf(posY), String.valueOf(posZ));
		Node node = this.graph.getNode(nodeId);

		// Adjunta el objeto a un nodo del mapa
		sprite.attachToNode(nodeId);

		// Añadimos el sentencia que se ejecutara en la simulación para este
		// evento
		this.addAttribute(step, node, attribute, values);
	}

	public void moveObjectFromTo(int step, String objectId, Vector point1, Vector point2, int totalStepTime) {
		// TODO Auto-generated method stub
		// Conseguimos el sprite/objeto
		Sprite sprite = this.spriteManager.getSprite(objectId);

		// Conseguimos la arista a través de la cual se va a mover el objeto
		String idNode1 = this.getNodeIdBasedOnMapPosition(String.valueOf(point1.getX()), String.valueOf(point1.getY()),
				String.valueOf(point1.getZ()));
		String idNode2 = this.getNodeIdBasedOnMapPosition(String.valueOf(point2.getX()), String.valueOf(point2.getY()),
				String.valueOf(point2.getZ()));
		String edgeId = this.getEdgeIdBasedOnMapPosition(idNode1, idNode2);
		Edge edge = this.graph.getEdge(edgeId);
		edge = (edge == null ? this.graph.getEdge(this.getEdgeIdBasedOnMapPosition(idNode2, idNode1)) : edge);
		System.out.println("Me quiero mover de " + point1 + " a " + point2);
		if (edge == null)
			throw new IllegalArgumentException(
					"La arista por la que desea mover su objeto no existe: " + idNode1 + idNode2);

		// Adjuntamos el sprite a la arista
		sprite.attachToNode(idNode2);

		// Añadimos el sentencia que se ejecutara en la simulación para este
		// evento
		Node sourceNode = this.graph.getNode(idNode1);
		Node targetNode = this.graph.getNode(idNode2);
		String attribute = "ui.class";
		String values = sprite.getAttribute(attribute);
		this.addAttribute(step, sourceNode, attribute, "vacio");
		this.addAttribute(step, targetNode, attribute, values);

	}

	public void removeObject(int timeStep, String identifier) {
		Node attachedNode = (Node) this.spriteManager.getSprite(identifier).getAttachment();
		String attribute = "ui.class";
		this.addAttribute(timeStep, attachedNode, attribute, "vacio");
		this.spriteManager.removeSprite(identifier);
	}

	public void addEmptyStep(int step) {

	}
}
