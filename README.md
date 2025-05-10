# Simulated Annealing Optimizer for Production Planning

<p align="center">
	<img src="https://img.shields.io/github/last-commit/JosueVazqJim/Simulated-Annealing?style=for-the-badge&logo=git&logoColor=white&color=ff0000" alt="last-commit">
	<img src="https://img.shields.io/github/languages/top/JosueVazqJim/Simulated-Annealing?style=for-the-badge&color=ff0000" alt="repo-top-language">
</p>
<p align="center">Built with the tools and technologies:</p>
<p align="center">
	<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" alt="java">
</p>

## 📖 About the Project

This JavaFX application implements the **Simulated Annealing (SA) algorithm** for robust temporal horizon decomposition in production planning problems, based on the research paper:

> *"A simulated annealing algorithm for the robust decomposition of temporal horizons in production planning problems"*  
> (Torres Delgado & Vélez Gallego, 2007)

![Continuous Optimization Example](/docs/images/app_runnin_1.png)
![Discrete Optimization Example](/docs/images/app_runnin_2.png)

## Key Features
- ✅ **Visual interface for configuring SA parameters**
- ✅ **Dynamic solution visualization**
- ✅ **Real-time optimization progress tracking**
- ✅ **Implementation of the paper's specific autonomy margin calculation**

## 🛠️ Implementation Details

### Core Algorithm
- Hybrid approach combining **Dynamic Programming** (initial solution) and **Simulated Annealing** (continuous optimization)
- Specialized neighbor generation preserving temporal adjacency constraints
- Accurate calculation of autonomy margin (W) per the paper's five cases

## Getting Started

### Prerequisites
- Java JDK 11+
- Maven 3.6+

### Installation
```bash
git clone https://github.com/yourusername/Simulated-Annealing.git
cd Simulated-Annealing
mvn clean package
```


## 🖥️ GUI Features
![Application Screenshot](screenshot.png) *(placeholder for actual screenshot)*

1. **Input Section**:
    - Task definition (Cₖ, Fₖ, Dₖ)
    - Initial solution E(π)
    - SA parameters (T₀, Tₚ, ε, iterations)

2. **Output Section**:
    - Best solution found
    - Iteration logs
    - Real-time charts:
        - Temperature decay
        - Autonomy margin progression

3. **Visualization**:
    - Interactive solution timeline
    - Operation load distribution graphs

## 🧪 Testing with Paper Examples

Use the sample data:

      Tasks:
      1,6,3
      3,10,4
      4,11,5
      6,10,2
      9,14,3
      12,15,2
      7,13,4
      
      Initial Solution: 1,3,7,11,14,15
      Parameters: T₀=1000, Tₚ=0.00001, ε=0.975, iterations=5


## 📚 Theoretical Background

The algorithm maximizes the autonomy margin (W) between maximum and minimum workloads across production intervals, where:

    W = Σ[W_max(Tᵢ,Tᵢ₊₁) - W_min(Tᵢ,Tᵢ₊₁)]


Key constraints:

    Temporal adjacency property (no operation spans >2 intervals)

    Continuous time optimization from discrete initial solution

## Performance Tips

- For continuous problems, use higher iteration counts
- Start with higher temperatures (1000+) for complex landscapes
- Adjust cooling rate between 0.9-0.99 for balance

## Contributing

We welcome contributions! Please:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request