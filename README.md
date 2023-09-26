# Processes and Logs Generator

[Process Log Generator](http://plg.processmining.it/) is a application capable to generate random business processes, starting from some general "complexity paramenters". PLG is also able to "execute" a given process model in order to generate a process log.

This software is designed to help researchers in the construction of a large set of processes and corresponding execution logs. This software is released with a small library which could help in the programmatical creation of processes.

More information at the project home page http://plg.processmining.it/.

**Attention:** this repository is a complete rewriting of the project already available at: https://github.com/delas/plg-old.

![Untitled](https://github.com/delas/plg/assets/867237/5d5dd159-826c-4b09-9de5-0b7176a3c5a6)

## Main features

* Random process generation, with different complexity parameters
* Random process evolution (to generate slight variations of existing processes)
* Configuration of time for activities duration and time between activities (via Python scripts)
* Generation of static/dynamic data objects for multi-perspective event log generation (via Python scripts)
* Import of process from
  * PLG file format
  * BPMN files (generated from SAP Signavio)
* Export of generated processes as
  * PLG file format
  * BPMN 2.0 XML file
  * BPMN as Graphviz Dot file
  * Petri net as Graphviz Dot file
  * Petri net as LoLA file
  * Petri net as PNML file
  * Petri net as TPN file
* Generation of an event log with any number of traces
* Fine-tuned configuration of noise parameters for event log generation
* Export of the generated event log as
  * XES file (both compressed as `.xes.gz` and not compressed as `.xes`)
  * MXML file (both compressed as `.mxml.gz` and not compressed as `.mxml`)
* Generation of an infinite stream of events
* Event streams generated as MQTT-XES format (cf. https://www.beamline.cloud/mqtt-xes/)
* Ability to dynamically switch the process generting the events (to simulate concept drift in streams)
* Generation of noise into the stream

## Help
* Visit the [Wiki](https://github.com/delas/plg/wiki) for all information. Useful quick documentation:
 * How to specify [Data Objects](https://github.com/delas/plg/wiki/Data-Objects-Definition) to generate multiperspectives logs
 * How to work with [times in PLG](https://github.com/delas/plg/wiki/Managing-Timestamps)

## Libraries
PLG makes use of the following libraries:
* [`libPlg`](https://github.com/delas/libPlg): library for processes and event generation
* [`libPlgStream`](https://github.com/delas/libPlgStream): library for stream generation
* [`libPlgVisualizer`](https://github.com/delas/libPlgVisualizer): library for process visualization

## Citation

Please, cite this work as:
* Andrea Burattin. "[PLG2: Multiperspective Process Randomization with Online and Offline Simulations](https://andrea.burattin.net/publications/2016-bpm-demo)". In *Online Proceedings of the BPM Demo Track* 2016; Rio de Janeiro, Brasil; September, 18 2016; CEUR-WS.org 2016.

Other relevant publications:
* Andrea Burattin. "[PLG2: Multiperspective Processes Randomization and Simulation for Online and Offline Settings](http://arxiv.org/abs/1506.08415)". In *CoRR* abs/1506.08415, Jun. 2015.
* Andrea Burattin and Alessandro Sperduti. "[PLG: a Framework for the Generation of Business Process Models and their Execution Logs](http://andrea.burattin.net/publications/2010-bpi)". In *Proceedings of the 6th International Workshop on Business Process Intelligence* (BPI 2010); Stevens Institute of Technology; Hoboken, New Jersey, USA; September 13, [2010.10.1007/978-3-642-20511-8_20](http://dx.doi.org/10.1007/978-3-642-20511-8_20).
