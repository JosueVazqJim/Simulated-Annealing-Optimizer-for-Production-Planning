package org.vazquezj.proyecto.recocido_simulado;

import lombok.Getter;
import lombok.Setter;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@Getter
@Setter
public class RecocidoSimulado {
	private final List<Operacion> planOperaciones;
	private List<Double> solucionActual;
	private List<Double> mejorSolucion;
	private final double[] horizontePlanificacion;
	private final double temperaturaInicial;
	private final double temperaturaFinal;
	private final int iteracionesPorTemperatura;
	private final float tasaEnfriamiento;
	private final Random random;
	private Consumer<String> logger;
	private List<Double> temperaturaPorIteracion = new ArrayList<>();
	private List<Double> margenAutonomiaPorIteracion  = new ArrayList<>();


	public RecocidoSimulado(List<Operacion> operaciones, List<Double> solucionInicial,
	                                  double temperaturaInicial, double temperaturaFinal,
	                                  float tasaEnfriamiento, int iteracionesPorTemperatura,
	                                  Consumer<String> logger) {
		this.planOperaciones = operaciones;
		this.solucionActual = new ArrayList<>(solucionInicial);
		this.mejorSolucion = new ArrayList<>(solucionInicial);
		this.horizontePlanificacion = new double[]{
				planOperaciones.stream().mapToDouble(Operacion::getC_k).min().orElse(0),
				planOperaciones.stream().mapToDouble(Operacion::getF_k).max().orElse(0)
		};
		this.temperaturaInicial = temperaturaInicial;
		this.temperaturaFinal = temperaturaFinal;
		this.tasaEnfriamiento = tasaEnfriamiento;
		this.iteracionesPorTemperatura = iteracionesPorTemperatura;
		this.random = new Random();
		this.logger = logger;
	}

	public List<Double> optimizar() {
		double temperatura = temperaturaInicial;
		int contadorIteracion = 0;

		while (temperatura > temperaturaFinal) {
			for (int i = 0; i < iteracionesPorTemperatura; i++) {
				List<Double> solucionVecina = generarSolucionVecina(solucionActual);

				double margenAutonomiaVecina = 0;
				double margenAutonomiaActual = 0;
				if (esFactible(solucionVecina)) {
					//evaluamos la funcion objetivo que es W o margen de autonomia de cada solucion
					margenAutonomiaActual = calcularAutonomia(solucionActual);
					margenAutonomiaVecina = calcularAutonomia(solucionVecina);

					if (debeAceptarSolucion(margenAutonomiaActual, margenAutonomiaVecina, temperatura)) {
						solucionActual = new ArrayList<>(solucionVecina);
					}

					if (margenAutonomiaVecina > calcularAutonomia(mejorSolucion)) {
						mejorSolucion = new ArrayList<>(solucionVecina);
					}
				}

				// Registro
				temperaturaPorIteracion.add(temperatura);
				margenAutonomiaPorIteracion.add(calcularAutonomia(solucionActual));
				logger.accept(String.format(
						"Iter %d | T = %.5f | ΔW = %.4f | Mejor W = %.4f | Vecino = %s",
						++contadorIteracion,
						temperatura,
						margenAutonomiaVecina - margenAutonomiaActual,
						calcularAutonomia(mejorSolucion),
						solucionVecina
				));
			}
			temperatura *= tasaEnfriamiento;
		}
		return mejorSolucion;
	}

	//va a alterar la solucion actual y solo un elemento que no sea el primero ni el ultimo
	private List<Double> generarSolucionVecina(List<Double> solucionActual) {
		List<Double> solucionVecina = new ArrayList<>(solucionActual);
		int indice = random.nextInt(solucionActual.size() - 2) + 1; // Evitar el primer y último índice
		double nuevoValor = solucionVecina.get(indice - 1) + random.nextDouble() *
				(solucionVecina.get(indice + 1) - solucionVecina.get(indice - 1));
		solucionVecina.set(indice, nuevoValor);
		return solucionVecina;
	}

	//verifica la adyacencia del plan de operaciones, que ninguna operacion se cruce mas de 2 veces
	private boolean esFactible(List<Double> solucion) {
		for (Operacion op : planOperaciones) {
			List<Integer> intervalos = new ArrayList<>();
			for (int i = 0; i < solucion.size() - 1; i++) {
				if (op.getC_k() < solucion.get(i + 1) && op.getF_k() > solucion.get(i)) {
					intervalos.add(i);
				}
			}
			// Si hay más de dos intervalos NO consecutivos, la solución no es válida
			if (intervalos.size() > 2) return false;
			if (intervalos.size() == 2 && intervalos.get(1) - intervalos.get(0) > 1) return false;
			if (intervalos.size() > 2) return false; // redundancia defensiva
		}
		return true;
	}


	public double calcularAutonomia(List<Double> solucion) {
		double autonomia = 0;
		for (int i = 0; i < solucion.size() - 1; i++) {
			double wmin = 0;
			double wmax = 0;
			for (Operacion op : planOperaciones) {
				// Casos 4 y 5 del artículo (contribuyen al margen)
				if (op.getF_k() <= solucion.get(i)) continue; // Caso 5: la operación termina antes del intervalo actual
				if (op.getC_k() >= solucion.get(i + 1)) continue; // Caso 4: la operación comienza en el siguiente intervalo o después

				// Intersección real de la tarea con el intervalo
				double inicioIntervalo = solucion.get(i);
				double finIntervalo = solucion.get(i + 1);
				double inicioTarea = Math.max(op.getC_k(), inicioIntervalo);
				double finTarea = Math.min(op.getF_k(), finIntervalo);
				double duracionDisponible = Math.max(0, finTarea - inicioTarea);

				// Si hay intersección posible
				if (duracionDisponible > 0) {
					// Calcular wmax
					wmax += Math.min(duracionDisponible, op.getD_k());

					// Calcular wmin
					double sobraInicio = Math.max(0, inicioIntervalo - op.getC_k());
					double sobraFin = Math.max(0, op.getF_k() - finIntervalo);
					double cargaMinima = Math.max(0, op.getD_k() - sobraInicio - sobraFin);

					wmin += Math.min(cargaMinima, duracionDisponible);
				}
			}
			autonomia += (wmax - wmin);
		}
		return autonomia;
	}

	// Criterio de aceptación de Metropolis
	private boolean debeAceptarSolucion(double margenActual, double margenVecino, double temperatura) {
		if (margenVecino > margenActual) return true;
		return Math.exp((margenVecino - margenActual) / temperatura) > random.nextDouble();
	}

}
