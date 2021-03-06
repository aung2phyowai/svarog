/**
 *
 */
package org.signalml.plugin.loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class describes the plug-in. Extends the {@link PluginState state} of
 * the plug-in.
 * Apart from parameters of the state, contains:
 * <ul>
 * <li>the string with the full name of the class,
 * that will be loaded to register the plug-in,</li>
 * <li>the name of the jar file with the plug-in,</li>
 * <li>the package that is exported by the plug-in,</li>
 * <li>the list of {@link PluginDependency dependencies} of the plug-in.</li>
 * </ul>
 * The parameters of this description are read from an XML file.
 * This description allows to:
 * <ul>
 * <li>check if its dependencies are satisfied,</li>
 * <li>find missing dependencies.</li>
 * </ul>
 * @author Marcin Szumski
 */
public class PluginDescription extends PluginState {
	private static final Logger logger = Logger.getLogger(PluginDescription.class);

	/**
	 * File this description has been loaded from.
	 */
	private File descriptionFile;

	/**
	 * the string with the full name of the class,
	 * that will be loaded to register the plug-in
	 */
	private String startingClass = null;
	/**
	 * the name of the jar file with the plug-in
	 */
	private String jarFile =  null;

	/**
	 * {@link #jarFile} URL.
	 */
	private URL jarFileURL;
	/**
	 * the name of the package that is exported by the plug-in
	 */
	private String exportPackage;

	/**
	 * the list of {@link PluginDependency dependencies}
	 * of the plug-in
	 */
	private ArrayList<PluginDependency> dependencies = new ArrayList<PluginDependency>();

	/**
	 * Plugin descriptor. This helps with dependency management during loading.
	 */
	private PluginHead pluginHead;

	/**
	 * Functions which checks if a variable is null and (if it is) adds its
	 * name to the string.
	 * @param missingValues the string enlisting missing values
	 * @param variable the variable to be checked
	 * @param variableName the name of the variable
	 */
	private String addMissing(String missingValues, Object variable, String variableName) {
		if (variable == null) {
			if (missingValues.length() != 0)
				missingValues += ", ";
			missingValues += variableName;
		}
		return missingValues;
	}

	/**
	 * Constructor. Parses the XML file of a given path, which
	 * contains the description of the plug-in.
	 * @param fileName the path to an XML file with the
	 * description of the plug-in
	 * @throws ParserConfigurationException if a DocumentBuilder
	 * cannot be created
	 * @throws SAXException if the creation of document builder fails
	 * @throws IOException if an error while parsing the file occurs
	 * @throws ParseException if the xml file doesn't contain all necessary
	 * values
	 */
	public PluginDescription(String fileName) throws ParserConfigurationException,
		SAXException, IOException, ParseException {
		logger.info("loading description from " + fileName);
		File descFile = new File(fileName);
		setDescriptionFile(descFile);

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(descFile);
		Element element = document.getDocumentElement();
		element.normalize();
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals("name"))
				if (name == null)
					name = node.getFirstChild().getNodeValue().trim();
				else
					logger.warn("duplicate plugin name node in xml file: " + fileName + " Used only first node.");
			else if (node.getNodeName().equals("jar-file"))
				if (jarFile == null)
					jarFile = node.getFirstChild().getNodeValue().trim();
				else
					logger.warn("duplicate plugin jar-file node in xml file: " + fileName + " Used only first node.");
			else if (node.getNodeName().equals("version"))
				if (version == null)
					setVersion(node.getFirstChild().getNodeValue().trim());
				else
					logger.warn("duplicate plugin version node in xml file: " + fileName + " Used only first node.");
			else if (node.getNodeName().equals("starting-class"))
				if (startingClass == null)
					startingClass = node.getFirstChild().getNodeValue().trim();
				else
					logger.warn("duplicate plugin starting-class node in xml file: " + fileName + " Used only first node.");
			else if (node.getNodeName().equals("export-package"))
				exportPackage =node.getFirstChild().getNodeValue().trim();
			else if (node.getNodeName().equals("dependencies"))
				parseDependencies(node);
		}

		String missingValues = new String();
		missingValues = addMissing(missingValues, name, "name");
		missingValues = addMissing(missingValues, version, "version");
		missingValues = addMissing(missingValues, jarFile, "jar-file");
		missingValues = addMissing(missingValues, startingClass, "starting-class");
		if (missingValues.length() > 0)
			throw new ParseException("the xml file (" + fileName + ") doesn't contain all necessary values. Missing values: " + missingValues + ".");
	}

	/**
	 * Parses the subtree with dependencies starting from the given node.
	 * @param node XML node containing the dependencies of the plug-in
	 */
	private void parseDependencies(Node node) {
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node dependencyNode = nodeList.item(i);
			if (dependencyNode.getNodeName().equals("dependency")) {
				NodeList dependencyNodeList = dependencyNode.getChildNodes();
				String name = null;
				String minimumVersion = null;
				for (int j = 0; j < dependencyNodeList.getLength(); ++j) {
					Node nodeTmp = dependencyNodeList.item(j);
					if (nodeTmp.getNodeName().equals("name"))
						name = nodeTmp.getFirstChild().getNodeValue().trim();
					else if (nodeTmp.getNodeName().equals("version"))
						minimumVersion = nodeTmp.getFirstChild().getNodeValue().trim();
				}
				if (name != null && minimumVersion != null) {
					PluginDependency dependency = new PluginDependency(name, minimumVersion);
					dependencies.add(dependency);
				}
			}
		}
	}


	/**
	 * @return the full name of the starting class (class loaded to register this plug-in)
	 */
	public String getStartingClass() {
		return startingClass;
	}


	/**
	 * @return the name of the jar file with this plug-in
	 */
	public String getJarFile() {
		return jarFile;
	}

	/**
	 * @return the name of the package exported by this plug-in
	 */
	public String getExportPackage() {
		return exportPackage;
	}

	/**
	 * Tells if all dependencies of the described plug-in are
	 * satisfied by any of the plug-ins on the list
	 * @param descriptions the list of all descriptions of plug-ins
	 * @return true if all dependencies satisfied, false otherwise
	 */
	public boolean dependenciesSatisfied(ArrayList<PluginDescription> descriptions) {
		for (PluginDependency dep : dependencies) {
			if (!dep.satisfied(descriptions))
			{
				setActive(false);
				return false;
			}
		}
		return true;
	}

	/**
	 * Creates an arrayList containing the dependencies that are not
	 * satisfied.
	 * @param descriptions the list of all descriptions of plug-ins
	 * @return the created array
	 */
	public ArrayList<PluginDependency> findMissingDependencies(ArrayList<PluginDescription> descriptions) {
		ArrayList<PluginDependency> missingDependencies = new ArrayList<PluginDependency>();
		for (PluginDependency dep: dependencies) {
			if (!dep.satisfied(descriptions)) {
				missingDependencies.add(dep);
			}
		}
		return missingDependencies;
	}

	@Override
	public String toString() {
		// TODO where is this used? can you modify this string
		// to include description file name?
		return name.concat(" v").concat(versionToString());
	}

	/**
	 * Returns true if this plug-in is not dependent from any plug-in from the
	 * list.
	 * @param descriptions the list of plug-ins
	 * @return true if this plug-in is not dependent from any plug-in from the
	 * list, false otherwise
	 */
	public boolean notDependentFrom(ArrayList<PluginDescription> descriptions) {
		for (PluginDescription descr : descriptions) {
			if (dependentFrom(descr)) return false;
		}
		return true;
	}

	/**
	 * Returns true if this plug-in depends on the given plug-in.
	 * @param description the description of the plug-in
	 * @return true if this plug-in depends on the given plug-in,
	 * false otherwise
	 */
	public boolean dependentFrom(PluginDescription description) {
		for (PluginDependency dependency : dependencies) {
			if (dependency.getName().equals(description.getName())) return true;
		}
		return false;
	}

	private void setDescriptionFile(File f) {
		this.descriptionFile = f;
	}

	protected void setJarFileURL(URL u) {
		this.jarFileURL = u;
	}

	/**
	 * Returns the file this description has been loaded from. May be null.
	 * @return file this description has been loaded from (null if none)
	 */
	public File getDescriptionFile() {
		return descriptionFile;
	}

	/**
	 * Returns the URL of this plugin JAR file.
	 * @return URL of this plugin JAR file (or null)
	 */
	public URL getJarFileURL() {
		return jarFileURL;
	}

	/**
	 * Set URL based on plugin description and given directory.
	 * @return true if JAR file is found and can be read
	 */
	public boolean fillURL(File directory) {
		final String name = directory.toURI().toString().concat(this.getJarFile());
		final URL url;
		try {
			url = new URL(name);
		} catch (MalformedURLException e) {
			logger.error("failed to create URL for file "+name);
			logger.error("", e);
			return false;
		}

		File file = new File(directory, this.getJarFile());
		if (!file.exists()) {
			logger.error("File '" + file.getAbsolutePath() +
						 "' does not exist");
			return false;
		} else if (!file.canRead()) {
			logger.error("File '" + file.getAbsolutePath() +
						 "' cannot be read.");
			return false;
		} else {
			this.setJarFileURL(url);
			logger.info(this.toString() + " will use " + url);
			return true;
		}
	}

	protected List<PluginDependency> getDependencies() {
		return Collections.unmodifiableList(dependencies);
	}

	protected PluginHead getHead() {
		return pluginHead;
	}
	protected void setHead(PluginHead h) {
		this.pluginHead = h;
	}
}
