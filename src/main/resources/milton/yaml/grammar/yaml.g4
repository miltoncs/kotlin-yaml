grammar yaml;

composite_yaml: yaml_document+ ;
yaml_document: directive? body end? ;
directive: '---' STRING '\n' ;
end: '...' '\n'?;
body: entry+ ;
entry: key ':' value ;
key: STRING ;
value: SINGLE_LINE_LITERAL | STRING '\n' | list | '\n' ;



SINGLE_LINE_LITERAL: '"' [^\n]*? '"' | '\'' [^\n]*? '\'';
STRING: [\\S]+ ;