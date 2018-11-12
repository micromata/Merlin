# Merlin Core library

This is Merlin's core library used by all other modules of Merlin.

## Functionality
Merlin core contains the base library supporting:
* Reading Excel files with validation of the data (xls and xlsx).
* Creating new and manipulating existing Excel files (xls and xlsx).
* Importing Excel data in a convenient way for the user by comparing
  uploaded Excel data with already imported data (not yet implemented).
  Refer the merlin-app for a demo use-case (not yet implemented).
* Manipulating of existing Word documents (docx): replace regions.
* Word templates (docx) and the variable and template definition. Variable
  substitution is as well supported as conditionals (equals, greater-less etc.)
  for having dynamic content in the result word file. Support of serial letters.

## Excel validation
tbd. Refer e. g. TemplateDefinitionExcelReader as a full example of
reading and validating an excel file.
Refer e. g.  ExcelWorkbookTest for returning Excel files with marked validation
errors.

## Templating
### Glossar
* Templates (or template files) are Word files containing variables and conditionals such as
  > This is an example word content for __${user}__.
  >
  > __{if fox = "lazy"}__ The __${color}__ fox is lazy.__{endif}__


  See [EmploymentContractTemplate.docx](https://github.com/kreinhard/merlin/raw/master/examples/templates/EmploymentContractTemplate.docx)
  in the [examples](https://github.com/kreinhard/merlin/tree/master/examples/templates) directory.
* Template definitions (optional) describes variables and also so called dependent variables.
  An template can be assigned to a template definition for more functionality.

  See [EmploymentContractTemplate.xlsx](https://github.com/kreinhard/merlin/raw/master/examples/templates/EmploymentContractTemplate.xlsx)
  in the [examples](https://github.com/kreinhard/merlin/tree/master/examples/templates) directory.
* Variables are customizable by the end-user running a template process. All variables in
  the template file will be replaced by the customized values and all conditionals will be executed
  by applying the variables.
* Dependent variables are defined inside template definitions. These variables are dependent from
  variables, e. g. a salutation in a letter is dependent on the gender of the receiver: for female
  receivers 'Dear Miss' and for male receivers 'Dear Mister' should be used. Dependent variables
  will be set automatically dependent on the assigned variable and has to be defined once in the template
  definition file.
* Serial letters: You may run templates multiple times for different sets of variables (such as letters for
  many receivers.) The variables are given in a table, each set of variables for a single run per row.
  
  See [EmploymentContract-Serial.xlsx](https://github.com/kreinhard/merlin/raw/master/examples/templates/EmploymentContract-Serial.xlsx)
  in the [examples](https://github.com/kreinhard/merlin/tree/master/examples/templates) directory.
* You may place comments in your word template document as well (such comments will be removed automatically). This is
  useful for documenting your template. Comments are enclosed in __{\* ...}__. You may optional end with __\*}__.
  > __{if fox = "lazy"}__ The fox is lazy. {\* here ends the lazy fox part. \*}__{endif}__
    
  Please note: Comments are only inside single paragraphs supported. Comments starting in one word paragraph and ending
  in a following one doesn't work. If you want to have comments in several paragraphs, please try this:
  > Here is my text.
  > {\* Here is my comment, which will be removed automatically. \*}
  >
  > {\* But I have to say it in more than one paragraph. \*}
  
  Please note: Comments containing hyperlinks aren't supported.


### Running a template
#### Single run
You may run a template with the following settings:
* Customized variables (gender, name of receiver, etc.)
* A template file. This template file may contain a reference to an existing template definition:
  > ${templateDefinition.refid = "Employment contract template"} (template definition referenced by id)
    
  Template definitions are automatically assigned if they have the same file name (without file extension) as the template
   file, e. g. a template named EmploymentContractTemplate.docx matches a definition file named EmploymentContractTemplate.xlsx.
* Optional a template definition file containing dependent variables.
  * You may give the template definition file directly or, if not
  * in the template file itself.
* Please notice: A template definition file is only needed:
  * if you want to use dependent variables and/or
  * if you want a more convenient user interface: Merlin validates all variables set by the user, such as:
    * Checking the correct type of the user's input: texts, selections, boolean, numbers (including optional min an max values), etc.
    * Flags such as required variables or unique flag (useful for the serial letter functionality).
#### Serial letter run
You may run a template with the following settings:
* Serial definition file containg variables. The template and template definition file may be specified
  inside this definition file (Excel), each set of variables per row.
* A template file to run (required if not defined in the serial definition file).
* Optional a template definition file containing dependent variables.
  * You may give the template definition file directly or, if not
  * it can be defined in the serial definition file or, if not
  * in the template file itself.
* Please notice: A template definition file is optional (see above).

### Template files
Template files (docx) may contain following expressions supported by Merlin:

| Expression | Description |
|------------|-------------|
|```${templateDefinition.refid = "..."}```|You may specify an optional template definition for further functionality for this template such as dependent variables.||
|```${id = "..."}```|You may specify id for this template for using as reference in serial letter runs.||
|```${variable}```|Will be replaced by the value of the variable if given.|
|```{if expr}...{endif}```|The text inside the if-endif-statement will be displayed only and only if the expression will be evaluated to true.

Examples for if-expressions:
* ```variable='value'```: Will be true, if the variable has the given value.
* ```variable!='value'```: Will be true, if the variable has __not__ the given value.
* ```variable in "blue", "red", "yellow"```: Will be true, if the variable has one of the given values.
* ```variable ! in "blue", "red", "yellow"```: Will be true, if the variable has none of the given values.
* Cascading of if-statements is supported.
* ```variable < value```, ```variable <= value```, ```variable > value```, ```variable >= value```. Integers and doubles as values are supported.
* ```variable```: Will be true, if variable is given and not blank.
* ```!variable```: Will be true, if variable is not given or blank. You may also write ```{if not variable}```
* With the not-operator (before variable name) you may negate all expressions: ```if{!variable>5}``` or ```if{not variable>5}```.

