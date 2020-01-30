decls   ->  decls decl

decl    ->  type id
        |   **extern** func
        |   **def** func

func    ->  type **name**(args);

args    ->  args arg
         |  null

arg     ->  type **id**