<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">



package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>

<xsl:result-document href="">
package <xsl:value-of select="//task/Package"/>;
<xsl:apply-templates select="//Import"/>

public class JavaFileTest {

    public static void main(String[] args) {.
        System.out.println("SICS");
    }

}

</xsl:result-document>



</xsl:template>
<xsl:template match="Import">
import <xsl:value-of select="."/>;
</xsl:template>
</xsl:stylesheet>
