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
 * CMIS-SQL lexer.
 */
lexer grammar CmisSqlLexer;

tokens {
    TABLE;
    COL;
    LIST;
    FUNC;
    UN_OP;
    BIN_OP;
    BIN_OP_ANY;
    NOT_IN;
    NOT_LIKE;
    IS_NULL;
    IS_NOT_NULL;
    ORDER_BY;
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

// ----- Generic SQL -----

SELECT : 'SELECT';
FROM : 'FROM';
AS : 'AS';
JOIN : 'JOIN';
INNER : 'INNER';
OUTER : 'OUTER';
LEFT : 'LEFT';
RIGHT : 'RIGHT';
ON : 'ON';
WHERE : 'WHERE';
ORDER : 'ORDER';
BY : 'BY';
ASC : 'ASC';
DESC : 'DESC';

// ----- Operators -----
IS : 'IS';
NULL : 'NULL';
AND : 'AND';
OR : 'OR';
NOT : 'NOT';
IN : 'IN';
LIKE : 'LIKE';
ANY : 'ANY';
CONTAINS : 'CONTAINS';
SCORE : 'SCORE';
IN_FOLDER : 'IN_FOLDER';
IN_TREE : 'IN_TREE';
UPPER : 'UPPER';
LOWER : 'LOWER';

STAR : '*';
LPAR : '(';
RPAR : ')';
COMMA : ',';
DOT : '.';
EQ : '=';
NEQ : '<>';
LT : '<';
GT : '>';
LTEQ : '<=';
GTEQ : '>=';

ID :
    ('a'..'z'|'A'..'Z'|'_')
    ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|':')*
    ;

NUM_LIT : '0' | '-'? ('1'..'9')('0'..'9')*;

STRING_LIT : '\'' (~'\''|'\'\'')* '\'';

WS : ( ' ' | '\t' | '\r'? '\n' )+ { $channel=HIDDEN; };
