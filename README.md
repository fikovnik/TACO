# TACO - documenTation generAtor for eCore mOdel

TACO is an Eclipse plugin that contributes a set of actions to automatically generate documentation for ecore models.

_The inspiration for this name was following painting of EMF (Emotional Mexican
Food) by SongofRedFire._

<a href="http://songofredfire.deviantart.com/art/EMF-The-Blue-Taco-194387520"><img align="center" href="http://fc03.deviantart.net/fs71/f/2011/021/e/b/emf__the_blue_taco_by_songofredfire-d37qedc.png" /></a>

<a href="http://songofredfire.deviantart.com/art/EMF-The-Blue-Taco-194387520"><img align="center" href="http://fc03.deviantart.net/fs71/f/2011/021/e/b/emf__the_blue_taco_by_songofredfire-d37qedc.png" width="65%" height="65%" /></a>

## Requirements

In order to be able to generate class diagrams, [Graphwiz](http://www.graphviz.org/) >= 2 has to be installed.

## Installation

* Update site: `http://fikovnik.net/www/projects/TACO/update-site/`

## Building from source

The project is maven built. It can be built from a command line:

```
mvn -f src/net.fikovnik.projects.taco.parent/pom.xml clean install
```

The resulting update site will be in the `src/net.fikovnik.projects.taco.update-site/target/site`

Another possibility is to use Eclipse using the [M2E
plugin](http://eclipse.org/m2e/). In the `net.fikovnik.projects.taco.ui` is a
custom launch configuration that can help to get started (for OSX only). Before
playing with the source in Eclipse, it is good to set a target platform to
`e37.target` that is located in the
`net.fikovnik.projects.taco.target-platform`.

## Features

* Export LaTeX documentation including class diagrams
* Export class diagram of a selected package or set of classes into a PDF or PNG document

It extracts documentation directly from an Ecore model using GenModel annotations:

```
<eAnnotations source="http://www.eclipse.org/emf/2002/GenModel";>
  <details key="documentation" value="Documentation that will be used by TACO and GenModel as well."/>
</eAnnotations>
```

## Limitations

_The motivation for this work was to be able to generate documentation for a
particular set of models I'm working on in the lab. Although they are quite
complex they do not contain enums, datatypes or nested packages. I will add
these features once I have model that uses them._

* No support nested packages
* No support for Enums
* No support for DataTypes

Any feedback or contribution is welcomed!
