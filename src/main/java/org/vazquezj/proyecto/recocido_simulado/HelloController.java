package org.vazquezj.proyecto.recocido_simulado;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class HelloController implements Initializable {

	@FXML private TextArea txtTareas;
	@FXML private TextField txtSolucionInicial;
	@FXML private TextField txtInitialTemp;
	@FXML private TextField txtFinalTemp;
	@FXML private TextField txtCoolingRate;
	@FXML private TextField txtIterTemp;
	@FXML private TextArea txtOutput;
	@FXML private TextArea txtLog;
	@FXML private VBox chartContainer;

	@FXML
	protected void onRunSimulation(ActionEvent event) {
		try {
			txtLog.clear();
			txtOutput.clear();

			// Leer tareas del TextArea
			List<Operacion> tareas = parsearTareas(txtTareas.getText().trim());
			if (tareas.isEmpty()) throw new IllegalArgumentException("Debes ingresar al menos una tarea.");

			// Leer E(pi) como lista de tiempos
			List<Double> cortesIniciales = Arrays.stream(txtSolucionInicial.getText().split(","))
					.map(String::trim)
					.map(Double::parseDouble)
					.sorted()
					.collect(Collectors.toList());

			if (cortesIniciales.size() < 2)
				throw new IllegalArgumentException("La solución inicial debe tener al menos dos puntos de corte.");

			// Leer parámetros
			double T0 = Double.parseDouble(txtInitialTemp.getText().trim());
			double Tf = Double.parseDouble(txtFinalTemp.getText().trim());
			float epsilon = Float.parseFloat(txtCoolingRate.getText().trim());
			int iteraciones = Integer.parseInt(txtIterTemp.getText().trim());

			// Crear instancia del Recocido Simulado
			RecocidoSimulado recocido = new RecocidoSimulado(
					tareas,
					cortesIniciales,
					T0,
					Tf,
					epsilon,
					iteraciones,
					msg -> txtLog.appendText(msg + "\n")
			);

			List<Double> mejor = recocido.optimizar();
			double autonomiaFinal = recocido.calcularAutonomia(mejor);

			txtOutput.setText("Mejor solución: E(pi) = " + mejor +
					"\nMargen de autonomía: " + String.format("%.4f", autonomiaFinal));

			mostrarGraficos(recocido.getTemperaturaPorIteracion(), recocido.getMargenAutonomiaPorIteracion());

		} catch (Exception e) {
			txtOutput.setText("⚠️ Error: " + e.getMessage());
		}
	}

	private List<Operacion> parsearTareas(String texto) {
		List<Operacion> lista = new ArrayList<>();
		String[] lineas = texto.split("\\n");
		for (String linea : lineas) {
			String[] partes = linea.split(",");
			if (partes.length != 3) continue;

			double Ck = Double.parseDouble(partes[0].trim());
			double Fk = Double.parseDouble(partes[1].trim());
			double Dk = Double.parseDouble(partes[2].trim());
			lista.add(new Operacion(Ck, Fk, Dk, 1));
		}
		return lista;
	}

	private void mostrarGraficos(List<Double> temperaturas, List<Double> valores) {
		chartContainer.getChildren().clear();

		NumberAxis xAxis1 = new NumberAxis();
		NumberAxis yAxis1 = new NumberAxis();
		LineChart<Number, Number> tempChart = new LineChart<>(xAxis1, yAxis1);
		tempChart.setTitle("Temperatura vs Iteración");
		XYChart.Series<Number, Number> tempSeries = new XYChart.Series<>();
		for (int i = 0; i < temperaturas.size(); i++) {
			tempSeries.getData().add(new XYChart.Data<>(i, temperaturas.get(i)));
		}
		tempChart.getData().add(tempSeries);

		NumberAxis xAxis2 = new NumberAxis();
		NumberAxis yAxis2 = new NumberAxis();
		LineChart<Number, Number> margenChart = new LineChart<>(xAxis2, yAxis2);
		margenChart.setTitle("Margen de Autonomía vs Iteración");
		XYChart.Series<Number, Number> margenSeries = new XYChart.Series<>();
		for (int i = 0; i < valores.size(); i++) {
			margenSeries.getData().add(new XYChart.Data<>(i, valores.get(i)));
		}
		margenChart.getData().add(margenSeries);

		chartContainer.getChildren().addAll(tempChart, margenChart);
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		txtLog.setText("📌 Ingresa las tareas y parámetros para comenzar.\n");
	}
}
