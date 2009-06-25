/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 *     Stefane Fermigier, Nuxeo
 */
/**
 * First try at a CMIS-SQL grammar.
 * Directly translated from the CMIS 0.5 EBNF specs.
 * Some rules have already been changed (or temporarily disabled) while
 * we work around left-recursion and other issues.
 */
grammar CmisSql;

options {
    language = Java;
    output = AST;
}

@header {
package org.apache.chemistry.cmissql;
}
@lexer::header {
package org.apache.chemistry.cmissql;
}

// LEXER

ID
    : ('a'..'z'|'A'..'Z'|'_')('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

SIGNED_NUMERIC_LITERAL
    : '0'
    | '-'? ('1'..'9')('0'..'9')*
    ;

CHARACTER_STRING_LITERAL
    : '\'' ( ~'\'' | '\'\'')* '\''
    ;

WS  : ( ' ' | '\t' | '\r'? '\n' )+ { $channel=HIDDEN; }
    ;

// PARSER

query: simple_table order_by_clause?;

simple_table
    : 'SELECT'^ select_list from_clause where_clause?
    ;

select_list
    : '*'
      -> '*'
    | select_sublist ( ',' select_sublist )*
      -> ^(select_sublist select_sublist*)
    ;

select_sublist
    : value_expression ( 'AS'!? column_name )?
    | qualifier '.*'
    //| multi_valued_column_reference
    ;

value_expression:
      column_reference
    | string_value_function
    | numeric_value_function
    ;

column_reference:
    ( qualifier '.' )? column_name;

multi_valued_column_reference:
    ( qualifier '.' )? multi_valued_column_name;

string_value_function:
    ( 'UPPER' | 'LOWER' )? '('! column_reference ')'!;

numeric_value_function:
    'SCORE()';

qualifier:
      table_name
    //| correlation_name
    ;

// FROM stuff

from_clause: 'FROM'^ table_reference;

// Use same trick as http://antlr.org/grammar/1057936474293/DmlSQL2.g to
// remove left recursion.
table_reference
    : table_name ( 'AS'!? correlation_name )?
    | joined_table
    ;

joined_table
    : '(' joined_table ')'
    //| table_reference join_type? 'JOIN' table_reference join_specification?
    ;

join_type: 'INNER' | 'LEFT' 'OUTER'?;

join_specification:
    'ON'^ '('! column_reference '='! column_reference ')'!;

// WHERE stuff

where_clause: 'WHERE'^ search_condition;

// Rewritten from the spec to avoid left-recursion
search_condition:
    boolean_term ( 'OR'^ boolean_term )*;

// Rewritten from the spec to avoid left-recursion
boolean_term:
    boolean_factor ( 'AND'^ boolean_factor )*;

boolean_factor:
    'NOT'^?  boolean_test;

boolean_test:
      predicate
    | '('! search_condition ')'!
    ;

predicate:
      comparison_predicate
    | in_predicate
    | like_predicate
    | null_predicate
    | quantified_comparison_predicate
    | quantified_in_predicate
    | text_search_predicate
    | folder_predicate
    ;

comparison_predicate:
    value_expression comp_op^ literal;

comp_op:
    '=' | '<>' | '<' | '>' | '<=' | '>=';

literal:
      SIGNED_NUMERIC_LITERAL
    | CHARACTER_STRING_LITERAL
    ;

in_predicate:
    column_reference 'NOT'? 'IN'^ '('! in_value_list ')'!;

in_value_list:
    literal ( ','! literal )*;

like_predicate:
    column_reference 'NOT'? 'LIKE'^ CHARACTER_STRING_LITERAL;

null_predicate
    // second alternative commented out to remove left recursion for now.
    //( column_reference | multi_valued_column_reference ) 'IS' 'NOT'? 'NULL';
    : column_reference 'IS'^ 'NOT'? 'NULL'
    ;

quantified_comparison_predicate:
    literal comp_op^ 'ANY' multi_valued_column_reference;

quantified_in_predicate:
    'ANY' multi_valued_column_reference 'NOT'? 'IN'^ '('! in_value_list ')'!;

text_search_predicate:
    'CONTAINS'^ '('! qualifier? ','! text_search_expression ')'!;

folder_predicate:
    ( 'IN_FOLDER' | 'IN_TREE' )^ '(' qualifier? ',' folder_id ')';

order_by_clause:
    'ORDER BY'^ sort_specification ( ','! sort_specification )*;

sort_specification:
    column_name ( 'ASC' | 'DESC' )?;

correlation_name:
    ID;

table_name:
    ID;

column_name:
    ID;

multi_valued_column_name:
    ID;

folder_id:
    CHARACTER_STRING_LITERAL;

text_search_expression:
    CHARACTER_STRING_LITERAL;
