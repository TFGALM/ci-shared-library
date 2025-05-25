# bb-ci-shared-library

La pipeline comprueba si el origen es un branch valido. 
La funcion validationService.isValidBranch comprueba si el nombre del branch es valido o si el evento tiene "refs/tags/".
Si las comprobaciones son exitosas se procede a clonar el contenido de la rama, en caso contrario, devuelve error.

Se comprueba el tipo de evento es : 
- Merge (feature o bugfix)
- Tag 
- Hotfix.

- Los eventos tipo hotfix tienen una casuistica especial, estos crean un artefacto sin necesidad de mergear en main con el fin de poderlo desplegar directamete, el nombre se pone a traves de 'setAndPushHotfixTagVersion' con el pattern: 'hotfix_<nombre_del_repo>_timestamp'
- Los eventos de tipo push (TAG) son gestionados por la funcion manageTagPush que se encarga a través de git.getBranchName() de obtener el nombre del branch y clonarse el código vinculado al tag.

### Pruebas Pipeline

#### Ramas feature y/o bugfix

- Creación branch feature o bugfix:  La pipeline ejecuta los pasos clone repo, build artifact.
- Commit en el branch feature: La pipeline ejecuta: clone repo, build artifact.
- Creación de un tag personalizado: Sin el prefijo test_* la pipeline devuelve error. En caso contrario se ejecutan los pasos: clone repo, build artifact, build container image, publish artifact y publish image.
- Creación de una PR: Se ejecutan los siguentes pasos: clone repo, build artifact y Sonar Scanner.
- Mergear PR en la rama main : Se ejecuta los siguientes pasos: clone repo, build artifact, build container image, trivy, Publish artifact y Publish Image.

#### Rama hotfix

- Creación branch hotfix: La pipeline ejecuta: clone repo, build artifact
- Commit en el branch hotfix: La pipeline ejecuta: clone repo, build artifact, sonar scanner, build container, trivy, publish artifact y publish Image
- Creación de tag personalizado: No esta permitido crear tags personalizados desde una rama Hotfix 
- Creacion de PR desde branch hotfix: La pipeline ejecuta: clone repo, build artifact y Sonar Scanner.
- Añadir commits a la pr para validar la creacíon de tags por cada commit: Cada commit que se genere en la PR vuelve a ejecutar todos los pasos y genera el artefacto con el timestamp de la ejecucion.  
- Mergear la PR en la rama main: Ejecuta los steps: clone repo, build artifact, build container image,trivy, Publish artifact y Publish Image.
