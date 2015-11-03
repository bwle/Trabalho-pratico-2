
package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import javax.xml.bind.JAXBException;

import edu.unb.fga.dadosabertos.Camara;
import edu.unb.fga.dadosabertos.Deputado;
import edu.unb.fga.dadosabertos.Partido;

public class CamaraModel {
	
	private static CamaraModel instance;
	public static CamaraModel getInstance() {
		if (instance == null) {
			instance = new CamaraModel();
		}
		return instance;
	}
	
	private Camara camara;
	
	private List<Partido> partidos;
	private List<Integer> partidosCount;
	
	private ArrayList<CamaraModelDelegate> depsListeners = new ArrayList<CamaraModelDelegate>();
	private ArrayList<CamaraModelDelegate> partiesListeners = new ArrayList<CamaraModelDelegate>();
	
	private CamaraModel() {
		partidos = new ArrayList<Partido>();
		partidosCount = new ArrayList<Integer>();
		camara = new Camara();
		loadCamara();
	}
	
	public void loadCamara() {
		if (camara != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try { 
						System.out.println("Obtendo dados.");
						camara.obterDados();
						for (CamaraModelDelegate listener : depsListeners) {
							listener.updateData();
						}
						System.out.println("Dados iniciais obtidos.");
						for (Deputado deputado : getDeputados()) {
							deputado.obterDetalhes();
							Partido partido = deputado.getDetalhes().getPartido();
							if (!partidos.contains(partido)) {
								partidos.add(partido);
								partidosCount.add(new Integer(0));
								for (CamaraModelDelegate listener : partiesListeners) {
									listener.updateData();
								}
							}
							int index = partidos.indexOf(partido);
							partidosCount.set(index, partidosCount.get(index) + 1);
						}
						System.out.println(partidos);
					} catch (IOException e) {
						
					} catch (JAXBException e) {
						
					}
				}
			}).start();
		}
	}
	
	public List<Deputado> getDeputados() {
		return camara.getDeputados();
	}
	
	public List<Partido> getPartidos() {
		return partidos;
	}
	
	public Integer getCountForIndex(int index) {
		return partidosCount.get(index);
	}
	
	public void addListenerForDeps(CamaraModelDelegate listener) {
		if (!depsListeners.contains(listener)) {
			depsListeners.add(listener);
		}
	}
	
	public void addListenerForParties(CamaraModelDelegate listener) {
		if (!partiesListeners.contains(listener)) {
			partiesListeners.add(listener);
		}
	}
	
}