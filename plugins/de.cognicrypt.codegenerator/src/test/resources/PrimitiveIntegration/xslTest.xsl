<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">
<xsl:variable name="class"><xsl:value-of select="//SymmetricBlockCipher/name" />Cipher</xsl:variable>
<xsl:variable name="OutputFile"><xsl:value-of select="$class" />.java</xsl:variable>
<xsl:result-document href="transformedFile.txt">

<xsl:value-of select="//testRoot/test" /> is a success.</xsl:result-document>
</xsl:template>
</xsl:stylesheet>
