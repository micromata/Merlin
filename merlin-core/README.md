# Merlin Core

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

## Templating
### Glossar
* Templates (or template files) are Word files containing variables and conditionals such as
  > This is an example word content for __${user}__.
  > __{if fox = "lazy"}__ The __${color}__ fox is lazy.__{endif}__

  See [ContractTemplate.docx](https://github.com/kreinhard/merlin/raw/master/examples/ContractTemplate.docx)
  in the examples directory.
* Template definitions (optional) describes variables and also so called dependent variables.
  An template can be assigned to a template definition for more functionality.

  See [ContractDefinition.xlsx](https://github.com/kreinhard/merlin/raw/master/examples/ContractDefinition.xlsx)
  in the examples directory.
* Variables are customizable by the end-user running a template process. All variables in
  the template file will be replaced by the customized values and all conditionals will be executed
  by using the variables.
* Dependent variables are defined inside template definitions. These variables are dependent from
  variables, e. g. a salutation in a letter is dependent on the gender of the receiver. For female
  receivers 'Dear Miss' and for male receivers 'Dear Mister' should be used. Dependent variables
  will be set automatically dependent on the assigned variable and has to be defined once on template
  definition creation.

### Running a template
* You may run a template with the following settings:
  * Customized variables (gender, name of receiver, etc.)
  * A template file. This template file may contain a reference to an existing template definition.
    >
  * Optional a template definition file containing dependent variables.


