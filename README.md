# merlin
Magic and customer-friendly Excel, Word and configuration acrobatics.

## Excel validation (xls and xlsx)
* Supports validation of columns, such as cell type, flags like required, unique and pattern (e. g. E-Mail-Format)
* Supports visualization and highlighing inside the original Excel document:
  * Highlights the cell with validation errors and shows the validation error as comment.
  * Shows the validation errors of each cell of a row in a appended column.
  * The user can fix the validation errors and re-upload the same Excel. Merlin will remove the validation errors, highlightings etc.

## Word templates (docx) 
* Supports the replacement of variables (defined outside, e. g. in Excel or your program code).
* Supports if-statements for displaying or hiding parts of the Word document.

Example-template.docx
> This is an example word content for __${user}__.
> __{if fox = "lazy"}__ The _${color}_ fox is lazy.__{endif}__
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

## Word templates and serial letters
* Supports serial document function: Define your variables for a given Word template in Excel columns and
Merlin will create a Word document for each row.
This feature is under construction.

## Merlin desktop app
The Merlin desktop app is under construction. It will be a simple Desktop app for dummy users for
using Merlin for own Word templates.

## Configuring and templating with Excel
* configuration including configuration also with Excel, CSV and user-friendly Excel-Upload
* Via template mechanism you can create config files from the excel files.
