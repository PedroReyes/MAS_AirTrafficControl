package ro.ucv.bootstrap;


import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;


/**
 * This class starts the Jade framework and/or aditional containers on an existing one
 * feel free to extend/expand it if you want more functionality from it
 * @author Sorin
 *
 */

public class RunJade {
	//veti avea nevoie de runtime-ul jade-ului
	private jade.core.Runtime runtime = jade.core.Runtime.instance();
	private ContainerController home=null;
	private Profile p = new ProfileImpl();
	private String PORT="1099";

	/**
	 * 
	 * @return Returns the ContainerController of the container created in the constructor.
	 */
	public ContainerController getHome() {
		return home;
	}


	/**
	 * Acest constructor porneste un Main-Container sau Container Jade.
	 * 
	 * @param cuInterfata  cand acest parametru are valoarea true se porneste agentul RMA care controleaza interfata Jade. valabil doar daca main==true.
	 * @param main cand acest parametru are valoarea true containerul creat are rol de MainContainer. Deci se porneste o platforma noua.
	 * @param host Indica ip-ul sau numele calculatorului pe care ruleaza Main-Container pentru platforma la care se doreste conectarea. valabil doar pentru main==false.
	 * @param hostPort indica portul pe care ruleaza main container-ul 
	 * @param port Indica portul pe care sa ruleze jade pe aceasta masina(indiferent daca e Main-Container sau container auxiliar)
	 */
	public RunJade(boolean cuInterfata,boolean main, String host, String hostPort, String port){
		if(checkPort(port, "local"))PORT=port;
		if(main) runMainJade(cuInterfata);
		else runAuxJade(host, port);

	}

	/**
	 * Creates a JADE platform and it's MainContainer 
	 * 
	 * @param cuInterfata  cand acest parametru are valoarea true se porneste agentul RMA care controleaza interfata Jade.
	 * @param port Indica portul pe care sa ruleze jade pe aceasta masina
	 * 
	 */
	public RunJade(boolean cuInterfata, String port){
		if(checkPort(port, "local"))PORT=port;
		runMainJade(cuInterfata);
	}

	/**
	 * Creates an auxiliary container to the platform running on host:hostPort (where the MainContainer can be found)
	 * 
	 * @param host Indica ip-ul sau numele calculatorului pe care ruleaza Main-Container pentru platforma la care se doreste conectarea. 
	 * @param hostPort indica portul pe care ruleaza main container-ul 
	 * @param port Indica portul pe care sa ruleze JADE pe aceasta masina
	 */
	public RunJade( String host, String hostPort, String port){
		if(checkPort(port, "local"))PORT=port;
		runAuxJade(host, hostPort);

	}

	/**
	 * porneste o platforma Jade pe masina locala 
	 * @param cuInterfata cand acest parametru este true se instantiaza si agentul RMA(Remote Monitoring Agent) care va afisa o interfata pentru platforma
	 */
	protected void runMainJade(boolean cuInterfata){
		p.setParameter(p.LOCAL_PORT, PORT);
		home= runtime.createMainContainer(p);// acum ruleaza JADE pe calculatorul curent

		if(cuInterfata){
			//porneste interfata de la JADE (agentul rma)
			try {
				AgentController rma =home.createNewAgent("rma",	"jade.tools.rma.rma", new Object[0]);//am creat agentul
				rma.start();//acum a pornit
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}// termina de pornit interfata de la JADE

		}
	}

	/**
	 * porneste un container auxiliar la o platforma existenta ce ruleaza pe masina host
	 * @param host ip-ul
	 */
	protected void runAuxJade(String host, String hostPort){
		if(checkIP(host)){
			p.setParameter(p.MAIN, "false"); //nu e main container
			p.setParameter(p.LOCAL_PORT, PORT); // pe ce port vreti sa ruleze local? 
			if(checkPort(hostPort,"host"))
				p.setParameter(p.MAIN_PORT, hostPort); // pe ce port ruleaza MainContainer-ul
			// p.MAIN_HOST este o propietate de tip String cu numele calculatorului (sau ip-ul) pe care se afla Main-Container . sa zicem ca se numeste CalculatorulMainului,daca in consola Main Container-ului apare ip , folositi ip-ul
			p.setParameter(p.MAIN_HOST, host);  
			home = runtime.createAgentContainer(p);
		}
	}



	/**
	 * Checks the validity of a string that should specify a port. Doest not check the availability of the port.
	 * 
	 * @param s the String representing the port
	 * @return false if the String is not a number or is null or void.
	 */
	private boolean checkPort(String s, String portType){		
		if(s==null){
			System.out.println("The specified "+portType+" port is null. Running JADE on default port.");
			return false;
		}
		if(s.equals("")){
			System.out.println("The specified "+portType+" port is a void string. Running JADE on default port.");
			return false;
		}
		try{
			int x=Integer.parseInt(s);
			if(x>65535 || x<0){
				System.out.println("The specified "+portType+" port is not a number in the interval [0,65535]. Running JADE on default port.");
				return false;
			}
		}catch(NumberFormatException e){
			System.out.println("The specified "+portType+" port is not a number. Running JADE on default port.");
			return false;
		}		
		return true;
	}

	/**
	 * Checks the validity of a string that should specify an IP. Does not check the actual IP just the String.
	 * 
	 * @param s the String representing the IP
	 * @return false if the String is not a number or is null or void or represents a wrong number of bytes.
	 */
	private boolean checkIP(String s){		
		if(s==null){
			System.out.println("The specified IP is null.");
			return false;
		}

		if(s.equals("")){
			System.out.println("The specified IP is a void string.");
			return false;
		}

		String[] s1=s.split("[.]");
		if(s1.length!=4 && s1.length!=6) {
			System.out.println("The specified IP has "+s1.length+" bytes. It should have 4 or 6.");
			return false;
		}
		for(int i=0;i<s1.length;i++){
			try{
				int x= Integer.parseInt(s1[i]);
				if(x<0 || x>255){
					System.out.println("One of the bytes of the specified IP is not a number in the interval [0,255].");
					return false;
				}
			}catch(NumberFormatException e){
				System.out.println("One of the bytes of the specified IP is not a number.");
				return false;
			}		
		}
		return true;
	}



}
