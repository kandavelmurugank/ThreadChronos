# ThreadChronos (Thread Dump Analyzer)

An enterprise-grade, lightweight Java tool designed to analyze multiple JVM thread dumps. It identifies CPU hotspots, detects lock contention, and provides a visual timeline of thread states.

## 🚀 Features
* **Deep Stack Analysis**: Automatically filters out JVM/System noise (like `SocketInputStream`) to find bottlenecks in your actual application code.
* **Lock Contention Mapping**: Connects "Waiting" threads to the specific "Owner" thread holding the monitor.
* **Visual Timeline**: A color-coded dashboard showing how thread states (Runnable, Blocked, Waiting) evolve across snapshots.
* **OS Correlation**: Extracts `nid` and provides Decimal IDs to match Java threads with OS processes (like `top -H`).
* **Timestamped Reporting**: Generates unique HTML reports in the `/report` folder based on the execution time.

## 📁 Project Structure
* `src/com/mc/tdanalyzer/model`: Data objects (JavaThread, StackFrame, etc.)
* `src/com/mc/tdanalyzer/parse`: Regex-based logic for parsing JStack outputs.
* `src/com/mc/tdanalyzer/analyze`: Business logic for Hotspots and Timeline generation.
* `src/com/mc/tdanalyzer/report`: HTML and CSS generation logic.
* `thread_dumps/`: Input directory for your `.txt` or `.dump` files.
* `report/`: Output directory for generated analysis.

## 🛠️ Setup & Usage

### 1. Requirements
* **Java 1.8** or higher.
* No external dependencies (Pure Java).

### 2. Running the Tool
1.  Place your thread dump files into the `./thread_dumps` folder.
2.  Open the project in IntelliJ IDEA.
3.  Run the `Main.java` class.
4.  Open the latest file in the `report/` folder (e.g., `AnalysisReport_2026-03-06_143005.html`).

## 🔍 How to Interpret the Report
* **Green (R)**: Thread is `RUNNABLE`. If it stays green across all snapshots, it is likely saturating the CPU.
* **Red (B)**: Thread is `BLOCKED`. Look at the **Lock Contention** table to see who is holding the lock.
* **Yellow (W/T)**: Thread is `WAITING`. Usually idle (waiting for work or a timer).

---
*Created by Kandavel Murugan Kulandaivel*