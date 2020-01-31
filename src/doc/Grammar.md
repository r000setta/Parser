decls   ->  decls decl

decl    ->  type id
        |   **extern** func
        |   **def** func

func    ->  type **name**(defArgs);

defArgs    ->  defArgs defArg
         |  null

defArg     ->  type **id**

stmts   ->  stmts stmt
        |   null

stmt    ->  loc=bool
        |   **if**(bool) stmt
        |   **if**(bool) stmt else stmt
        |   **while**(bool) stmt
        |   **do** stmt **while**(bool);
        |   **break**;
        |   block
        |   call
        |   loc=call

loc     ->  loc[bool]
        |   **id**

call    ->  **name**(args);

args    ->  args **id**
        |   null
        
args    ->  **id**
        |   **id**,args