[![Run Status](https://api.shippable.com/projects/592827ff79553f0700b4c956/badge?branch=master)](https://app.shippable.com/github/CROSSINGTUD/CogniCrypt)

# CogniCrypt

CogniCrypt is an Eclipse plugin that supports Java developers in using cryptographic APIs. It supports developers in two ways. First, it may generate code snippets for a number of programming tasks that involve cryptography, e.g., communication over a secure channel, data encryption, and long-term archiving. Second, it continuously runs a suite of static analyses in the background that check the  developer's code for misuses of cryptographic APIs.

## Installation

The only way to currently set CogniCrypt up in Eclipse is to clone the repository and export it as a Jar file as described [here](https://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.pde.doc.user%2Fguide%2Ftools%2Fexport_wizards%2Fexport_plugins.htm).

## Contribution

Cryptography experts may contribute in two ways. Experts who design and implement cryptographic algorithms can integrate those into CogniCrypt. CogniCrypt does not expose these algorithms directly but through its tasks. Experts who are providing more high-level security solutions may integrate those as well into CogniCrypt. These will be made available to CogniCrypt's user directly as new tasks. Please refer to the [contributor's documentation](https://github.com/CROSSINGTUD/CogniCrypt-Documentation/blob/master/openccedoc.pdf) for more detail.