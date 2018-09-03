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

## Configuring and templating with Excel
 configuration including configuration also with Excel, CSV and user-friendly Excel-Upload
