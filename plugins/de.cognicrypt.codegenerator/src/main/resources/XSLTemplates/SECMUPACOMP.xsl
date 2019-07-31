<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">

<xsl:variable name="Rounds"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/iterations"/> </xsl:variable>
<xsl:variable name="outputSize"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/algorithm[@type='Digest']/outputSize"/> </xsl:variable>

<xsl:if test="//task[@description='SECMUPACOMP']">
package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {

<xsl:if test="//task[@description='SECMUPACOMP']//element[@type='SECMUPACOMP']//aby='Euclid'">
	public static double templateUsage(int pos_x, int pos_y <xsl:choose><xsl:when test="not(//task/code/host or //task/code/server='false')"></xsl:when>
         <xsl:otherwise>, String host</xsl:otherwise></xsl:choose> <xsl:choose><xsl:when test="//task/code/port"></xsl:when>
         <xsl:otherwise>, int port</xsl:otherwise></xsl:choose>, int bitlength ) {
        return Euc_dist.run(<xsl:choose>
        					<xsl:when test="//task/code/server='true'">0</xsl:when><xsl:otherwise>1</xsl:otherwise></xsl:choose>
        					, pos_x, pos_y, <xsl:value-of select="//task/element[@type='SECMUPACOMP']/security"/>, 
        					bitlength, 
        					<xsl:choose>
        					<xsl:when test="//task/code/host">"<xsl:value-of select="//task/code/host"/>"</xsl:when>
        					<xsl:when test="//task/code/server='true'">"This will be ignored."</xsl:when>
        					<xsl:otherwise>host</xsl:otherwise></xsl:choose>, 
        					<xsl:choose>
        					<xsl:when test="//task/code/port"><xsl:value-of select="//task/code/port"/></xsl:when>
        					<xsl:otherwise>port</xsl:otherwise>
        					</xsl:choose>);
	}
</xsl:if>
<xsl:if test="//task[@description='SECMUPACOMP']//element[@type='SECMUPACOMP']//aby='Millionaire'">
	public static int templateUsage(<xsl:choose><xsl:when test="not(//task/code/host or //task/code/server='false')"></xsl:when>
         <xsl:otherwise> String host, </xsl:otherwise></xsl:choose>int money) {
        
       return Mill_jni.run(<xsl:choose><xsl:when test="//task/code/server='true'">0</xsl:when><xsl:otherwise>1</xsl:otherwise></xsl:choose>, money);
	}
</xsl:if>
}
</xsl:if></xsl:template>
<xsl:template match="Import">
import <xsl:value-of select="."/>;
</xsl:template>
</xsl:stylesheet>