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
 *     Florent Guillaume, Nuxeo
 */
/**
 * CMIS-SQL parser.
 */
parser grammar CmisSqlParser;

options {
    tokenVocab = CmisSqlLexer;
    language = Java;
    output = AST;
}

@header {
/*
 * THIS FILE IS AUTO-GENERATED, DO NOT EDIT.
 *
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
 *     Florent Guillaume, Nuxeo
 *
 * THIS FILE IS AUTO-GENERATED, DO NOT EDIT.
 */
package org.apache.chemistry.cmissql;
}

query: simple_table order_by_clause?;

simple_table
    : SELECT^ select_list from_clause where_clause?
    ;

select_list
    : STAR
      -> STAR
    | select_sublist ( COMMA select_sublist )*
      -> ^(select_sublist select_sublist*)
    ;

select_sublist
    : value_expression ( AS!? column_name )?
    | qualifier DOT STAR
    //| multi_valued_column_reference
    ;

value_expression:
      column_reference
    | string_value_function
    | numeric_value_function
    ;

column_reference:
    ( qualifier DOT )? column_name;

multi_valued_column_reference:
    ( qualifier DOT )? multi_valued_column_name;

string_value_function:
    ( UPPER | LOWER )? LPAR! column_reference RPAR!;

numeric_value_function:
    SCORE LPAR RPAR;

qualifier:
      table_name
    //| correlation_name
    ;

// FROM stuff

from_clause: FROM^ table_reference;

// Use same trick as http://antlr.org/grammar/1057936474293/DmlSQL2.g to
// remove left recursion.
table_reference
    : table_name ( AS!? correlation_name )?
    | joined_table
    ;

joined_table
    : LPAR joined_table RPAR
    //| table_reference join_type? 'JOIN' table_reference join_specification?
    ;

join_type: INNER | LEFT OUTER?;

join_specification:
    ON^ LPAR! column_reference EQ! column_reference RPAR!;

// WHERE stuff

where_clause: WHERE^ search_condition;

// Rewritten from the spec to avoid left-recursion
search_condition:
    boolean_term ( OR^ boolean_term )*;

// Rewritten from the spec to avoid left-recursion
boolean_term:
    boolean_factor ( AND^ boolean_factor )*;

boolean_factor:
    NOT^?  boolean_test;

boolean_test:
      predicate
    | LPAR! search_condition RPAR!
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
    EQ | NEQ | LT | GT | LTEQ | GTEQ;

literal:
      SIGNED_NUMERIC_LITERAL
    | CHARACTER_STRING_LITERAL
    ;

in_predicate:
    column_reference NOT? IN^ LPAR! in_value_list RPAR!;

in_value_list:
    literal ( COMMA! literal )*;

like_predicate:
    column_reference NOT? LIKE^ CHARACTER_STRING_LITERAL;

null_predicate
    // second alternative commented out to remove left recursion for now.
    //( column_reference | multi_valued_column_reference ) 'IS' 'NOT'? 'NULL';
    : column_reference IS^ NOT? NULL
    ;

quantified_comparison_predicate:
    literal comp_op^ ANY multi_valued_column_reference;

quantified_in_predicate:
    ANY multi_valued_column_reference NOT? IN^ LPAR! in_value_list RPAR!;

text_search_predicate:
    CONTAINS^ LPAR! qualifier? COMMA! text_search_expression RPAR!;

folder_predicate:
    ( IN_FOLDER | IN_TREE )^ LPAR qualifier? COMMA folder_id RPAR;

order_by_clause:
    ORDER BY sort_specification ( COMMA sort_specification )*
      -> ^(ORDER_BY sort_specification+)
    ;

sort_specification:
    column_name ( ASC | DESC )?;

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
