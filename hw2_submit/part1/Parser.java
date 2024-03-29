/* *** This file is given as part of the programming assignment. *** */

public class Parser {

    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    private void scan() {
        tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
        this.scanner = scanner;
        scan();
        program();
        if( tok.kind != TK.EOF )
            parse_error("junk after logical end of program");
    }

    private void program() {
        block();
    }

    private void block(){
        declaration_list();
        statement_list();
    }

    private void declaration_list() {
        // below checks whether tok is in first set of declaration.
        // here, that's easy since there's only one token kind in the set.
        // in other places, though, there might be more.
        // so, you might want to write a general function to handle that.
        while( is(TK.DECLARE) ) {
            declaration();
        }
    }

    private void declaration() {
        mustbe(TK.DECLARE);
        mustbe(TK.ID);
        while( is(TK.COMMA) ) {
            scan();
            mustbe(TK.ID);
        }
    }

    private void statement_list(){
    	while(is(TK.ID)||
    			is(TK.LEVEL_REF)||
    			is(TK.PRINT)||
    			is(TK.DO)||
    			is(TK.IF))
    	{
    		statement();
    	}
    }
    private void statement(){
    	
		if (is(TK.ID) || is(TK.LEVEL_REF)){//assignment
			assignment();
		}else if(is(TK.PRINT)){
			print();
		}else if(is(TK.DO)){
			doo();
		}else if(is(TK.IF)){
			iif();
		}
    }

	private void assignment() {
		ref_id();
		mustbe(TK.ASSIGN);
		expr();
	}

    private void expr() {
    	term();
    	while(is(TK.PLUS) || is(TK.MINUS)){
    		addop();
    		term();
    	}
	}

	private void addop() {
		if(is(TK.PLUS)){
			scan();
		}else if(is(TK.MINUS)){
			scan();
		}
	}

	private void term() {
		factor();
		while(is(TK.TIMES) || is(TK.DIVIDE)){
			mulop();
			factor();
		}
	}

	private void mulop() {
		if(is(TK.TIMES)){
			scan();
		}else if(is(TK.DIVIDE)){
			scan();
		}
	}

	private void factor() {
		
		if(is(TK.LPAREN)){
			scan();
			expr();
			mustbe(TK.RPAREN);
		}else if(is(TK.LEVEL_REF) || is(TK.ID)){
			ref_id();
		}else if(is(TK.NUM)){
			scan();
		}else{
			parse_error("Illegal input @ line:" + tok.lineNumber);
		}
		
	}

	private void ref_id() {
		
		if(is(TK.LEVEL_REF)){
			scan();
			if(is(TK.NUM)){
				scan();
			}
		}		
		mustbe(TK.ID);		
	}

	private void iif() {
		mustbe(TK.IF);
		guarded_command();
		while(is(TK.ELSEIF)){
			scan();
			guarded_command();	
		}
		if(is(TK.ELSE)){
			scan();
			block();
		}
		mustbe(TK.ENDIF);
	}

	private void doo() {
		mustbe(TK.DO);
		guarded_command();
		mustbe(TK.ENDDO);
	}

	private void guarded_command() {
		expr();
		mustbe(TK.THEN);
		block();
	}

	private void print() {
		mustbe(TK.PRINT);
		expr();
	}

	// is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
        if( ! is(tk) ) {
            System.err.println( "mustbe: want " + tk + ", got " +
                                    tok);
            parse_error( "missing token (mustbe)" );
        }
        scan();
    }

    private void parse_error(String msg) {
        System.err.println( "can't parse: line "
                            + tok.lineNumber + " " + msg );
        System.exit(1);
    }
}
