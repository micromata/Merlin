# Merlin
Magic and customer-friendly Excel, Word and configuration acrobatics.

## Merlin packages
Merlin is organized as a multi module project.
* __merlin-core__ is a lightweight library to embed
awesome Excel- and Word-features in your own application.
* __merlin-app__ is a desktop application (with embedded web server and web app) for convenient Excel and Word acrobatics.
Some more modules for different use-cases.

## Excel validation (xls and xlsx)
* Supports validation of columns, such as cell type, flags like required, unique and pattern (e. g. E-Mail-Format)
* Supports visualization and highlighing inside the original Excel document:
  * Highlights the cell with validation errors and shows the validation error as comment.
  * Shows the validation errors of each cell of a row in a appended column.
  * The user can fix the validation errors and re-upload the same Excel. Merlin will remove the validation errors, highlightings etc.
* It's fully internationalized (all validation messages and hints will be localized). English and German is available.

### Example
* See folder __examples__: Test.xlsx is the original Excel file ```Test.xlsx``` and ```Test-result*.xls``` are the result files containing the localized validation errors.

## Word templates (docx) 
* Supports the replacement of variables (defined outside, e. g. in Excel or your program code).
* Supports if-statements for displaying or hiding parts of the Word document.

Example-template.docx
> This is an example word content for __${user}__.
> __{if fox = "lazy"}__ The __${color}__ fox is lazy.__{endif}__
> __{if color != "black"}__ The fox isn't black but he __{if fox = "lazy"}__ is __{endif}{if fox != "lazy"}__ isn't __{endif}__ lazy.
> __{endif}__

With the variables: $user="Horst", $color="red" and $fox="lazy" the result of your Word file should be:
> This is an example word content for __Horst__.
> The __red__ fox is lazy.
> The fox isn't black but he is lazy.

Supported expressions:

| Expression | Description |
|------------|-------------|
|${variable}|Will be replaced by the value of the variable if given.|
|{if expr}...{endif}|The text inside the if-endif-statement will be displayed only and only if the expression will be evaluated to true.

Examples for if-expressions:
* variable='value': Will be true, if the variable has the given value.
* variable!='value': Will be true, if the variable has __not__ the given value.
* variable in "blue", "red", "yellow": Will be true, if the variable has one of the given values.
* variable ! in "blue", "red", "yellow": Will be true, if the variable has none of the given values.
* Cascading of if-statements is supported.
* variable < value, variable <= value, variable > value, variable >= value. Integers and doubles as values are supported.

### Example
See the [examples](https://github.com/kreinhard/merlin/tree/master/examples/templates) with the template file [EmploymentContractTemplate.docx](https://github.com/kreinhard/merlin/raw/master/examples/templates/EmploymentContractTemplate.docx)
and the result file after processed by Merlin [EmploymentContract-Berta.docx](https://github.com/kreinhard/merlin/raw/master/examples/templates/EmploymentContract-Berta.docx).

## Word templates and serial letters
* Supports serial document function: Define your variables for a given Word template in Excel columns and
Merlin will create a Word document for each row.
This feature is under construction.

[Read more and see examples](./merlin-core)

## Merlin desktop app
The Merlin desktop app is a standalone app which contains an embedded web server and a modern React single page app.
Simply press the Start button to open the merlin app in your standard browser.
Drag&Drop is also supported as running a desktop file browser for configuring template directories as an example.
After starting the user can configure, run and do everything through a web browser.

A web version running on web servers for allowing teams to work with the same Merlin configuration is planned.

## Configuring and templating with Excel
* configuration including configuration also with Excel, CSV and user-friendly Excel-Upload
* Via template mechanism you can create config files from the excel files.

## merlin-smarthome
This is an example implementation of using Excel as configuration tool. In Excel all smart home items are defined.

Via templates the different configuration files for OpenHab are automatically generated.

Forget Copy&Paste and vi (in this context). Make your changes convenient in Excel and let do Merlin all the rest.

Please refer [OpenHab-KNX-Definitions.xlsx](https://github.com/kreinhard/merlin/raw/master/merlin-smarthome/examples/openhab-knx/OpenHab-KNX-Definitions.xlsx) for a first impression including the
result files: [open-knx](https://github.com/kreinhard/merlin/tree/master/merlin-smarthome/examples/openhab-knx)

# To-Do
* Multiple Template files (Word, AsciiDoc, Text etc.) for one TemplateDefinition.xlsx. (def-Tag in Word-Template?)
* Separate Java FX and web for supporting desktop version as well as a web server version.