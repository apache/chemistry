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
 *     Florent Guillaume, Nuxeo
 */
/**
 * CMIS-SQL tree grammar, walker for the simple implementation.
 */
tree grammar CmisSqlSimpleWalker;

options {
    tokenVocab = CmisSqlParser;
    ASTLabelType = CommonTree;
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
 *     Florent Guillaume, Nuxeo
 *
 * THIS FILE IS AUTO-GENERATED, DO NOT EDIT.
 */
package org.apache.chemistry.impl.simple;

import org.apache.chemistry.impl.simple.SimpleData;
}

@members {
    public SimpleData data;
}

query [SimpleData d] returns [String tableName, boolean matches]
@init {
    data = $d;
}:
    ^(SELECT select_list from_clause where_clause order_by_clause?)
    {
        $tableName = $from_clause.tableName;
        $matches = $where_clause.matches;
    }
    ;

select_list:
      STAR
    | ^(LIST select_sublist+)
    ;

select_sublist:
      value_expression column_name?
    | qualifier DOT STAR
    ;

value_expression returns [Object value]:
      column_reference
        {
            $value = $column_reference.value;
        }
//    | string_value_function
//    | numeric_value_function
    ;

column_reference returns [Object value]:
    ^(COL qualifier? column_name)
      {
          String col = $column_name.start.getText();
          $value = data.get(col); // TODO error if unknown prop
      }
    ;

qualifier:
      table_name
//    | correlation_name
    ;

from_clause returns [String tableName]:
    ^(FROM table_reference)
      {
          $tableName = $table_reference.tableName;
      }
    ;

table_reference returns [String tableName]:
      table_name
        {
            $tableName = $table_name.text;
        }
    | ^(TABLE table_name correlation_name)
        {
            $tableName = $table_name.text;
        }
    ;

where_clause returns [boolean matches]:
      ^(WHERE search_condition)
        {
            $matches = $search_condition.matches;
        }
    | /* nothing */
        {
            $matches = true;
        }
    ;

search_condition returns [boolean matches]:
      boolean_term
        {
            $matches = $boolean_term.matches;
        }
    | ^(OR (list+=boolean_term)+)
        {
            $matches = false;
            for (boolean_term_return t : (List<boolean_term_return>) $list) {
                if (t.matches) {
                    $matches = true;
                    break;
                }
            }
        }
    ;

boolean_term returns [boolean matches]:
      boolean_factor
        {
            $matches = $boolean_factor.matches;
        }
    | ^(AND (list+=boolean_factor)+)
        {
            $matches = true;
            for (boolean_factor_return t : (List<boolean_factor_return>) $list) {
                if (t.matches) {
                    $matches = false;
                    break;
                }
            }
        }
    ;

boolean_factor returns [boolean matches]:
      boolean_test
        {
            $matches = $boolean_test.matches;
        }
    | ^(NOT boolean_test)
        {
            $matches = ! $boolean_test.matches;
        }
    ;

boolean_test returns [boolean matches]:
      predicate
        {
            $matches = $predicate.matches;
        }
//    | search_condition
    ;

predicate returns [boolean matches]:
      ^(UN_OP IS_NULL un_arg)
        {
            $matches = $un_arg.value == null;
        }
    | ^(UN_OP IS_NOT_NULL un_arg)
        {
            $matches = $un_arg.value != null;
        }
    | ^(BIN_OP bin_op arg1=bin_arg arg2=bin_arg)
        {
            int token = $bin_op.start.getType();
            Object value1 = $arg1.value;
            Object value2 = $arg2.value;
            switch (token) {
                case EQ:
                    $matches = value1 != null && value1.equals(value2);
                    break;
                case NEQ:
                    $matches = value1 != null && value2 != null && ! value1.equals(value2);
                    break;
                default:
                    throw new UnwantedTokenException(token, input);
            }
        }
//    | text_search_predicate
//    | folder_predicate
    ;

un_arg returns [Object value]:
    column_reference
      {
          $value = $column_reference.value;
      }
    ;

bin_op:
    EQ | NEQ | LT | GT | LTEQ | GTEQ | LIKE | NOT_LIKE;

bin_arg returns [Object value]:
      value_expression
        {
            $value = $value_expression.value;
        }
    | literal
        {
            $value = $literal.value;
        }
    | ^(LIST (list+=literal)+)
        {
            List<Object> ret = new ArrayList<Object>($list.size());
            for (literal_return l : (List<literal_return>) $list) {
                ret.add(l.value);
            }
            $value = ret;
        }
    ;

literal returns [Object value]:
      NUM_LIT
        {
            $value = Long.valueOf($NUM_LIT.text);
        }
    | STRING_LIT
        {
            String s = $STRING_LIT.text;
            $value = s.substring(1, s.length() - 1);
        }
    ;

order_by_clause:
    ^(ORDER_BY sort_specification+)
    ;

sort_specification:
    column_name ( ASC | DESC )
    ;

correlation_name:
    ID;

table_name:
    ID;

column_name:
    ID;

multi_valued_column_name:
    ID;
