# CrySL Visual Order Editor

The Order Editor displays the usage pattern of the class as defined in the Order expression of its CrySL rule as a state machine.

## Set up

* Clone the project, open Eclipse.
* Clone the [**Crypto-API-Rules**](https://github.com/CROSSINGTUD/Crypto-API-Rules) repository to a folder named "git" in your home directory. 
  This is required since the class StaxWriter which writes the configuration file needs to access this different repository and therefore accesses the paths relative to home directory by e.g. "<home-dir>\\git\\Crypto-API-Rules\\JavaCryptographicArchitecture\\src".
* Install Sirius (http://www.eclipse.org/sirius) from the Eclipse Marketplace.
* Launch a new runtime from your Eclipse. Click on the black plugin icon "S" in the upper left corner, next to the other buttons for TaskIntegrator and CogniCrypt, 
  then click on the button "Generate Statemachine Models", which is currently a simple button but should later be replaced by a single crysl rule selection. The 
    button triggers the generation of the statemachine model resources into the output folder, i.e., "de.cognicrypt.order.editor\output".
* Within the runtime environment, select the Sirius perspective. This opens a model explorer in the left corner.
* Open the Sirius project contained in the plugin relative sirius 
  path, i.e. "CogniCrypt\plugins\de.cognicrypt.order.editor.sirius".
  The project folder "~\CogniCrypt\plugins\de.cognicrypt.order.editor\sirius\my.project.design" 
    is the Viewpoint Specification Project containing the .odesign file (definition of the modeling workbench), the other project folder
    "~\CogniCrypt\plugins\de.cognicrypt.order.editor\sirius\my.project.order.diagram.modeling" is the Modeling project containing the graphical representations created with Sirius.
* To open a diagram, select the file representations.aird from the modeling project. 
  In the left corner of the new window, named "Models", click on Add > Browse File System 
  to select a statemachine model from the plugin-relative output folder. The model will 
    appear in the Models window. Now double click "order" in the Representations window on the right. This opens a new window "Create a new representation" which allows to select a semantic element for a new representation. Here, you can select the model you just added, click on its "Statemachine" model identifier and click on Finish. You can optionally select a new name for the diagram. The representation is opened now and you can play around with the Sirius model editor features to enhace the representation.  
* For more information on Sirius, have a look at their, [**tutorial**](https://wiki.eclipse.org/Sirius/Tutorials/StarterTutorial). 