(ns help)

;--------------------------------------------------| HELP


(defn run [arg]
      (cond
        (= 0 (count arg)) (println "idiot: the other stupid content tracker\n\nUsage: idiot <command> [<args>]\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file -p <address>")
        (or (= "help" (nth arg 0)) (= "--help" (nth arg 0)) (= "-h" (nth arg 0))) (println "idiot help: print help for a command\n\nUsage: idiot help <command>\n\nArguments:\n   <command>   the command to print help for\n\nCommands:\n   help\n   init\n   hash-object [-w] <file>\n   cat-file -p <address>")
        (= "init" (nth arg 0)) (println "idiot init: initialize a new database\n\nUsage: idiot init\n\nArguments:\n   -h   print this message")
        (= "hash-object" (nth arg 0)) (println "idiot hash-object: compute address and maybe create blob from file\n\nUsage: idiot hash-object [-w] <file>\n\nArguments:\n   -h       print this message\n   -w       write the file to database as a blob object\n   <file>   the file")
        (= "cat-file" (nth arg 0)) (println "idiot cat-file: print information about an object\n\nUsage: idiot cat-file -p <address>\n\nArguments:\n   -h          print this message\n   -p          pretty-print contents based on object type\n   <address>   the SHA1-based address of the object")
        :else (println "Error: invalid command")))
