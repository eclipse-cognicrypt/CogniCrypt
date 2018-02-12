<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="text" />
	<xsl:template match="/">

		<xsl:result-document href="Provider.java">

			import java.security.Provider;

			public class CustomProvider extends Provider {

			private static final long serialVersionUID = -67123468605911408L;

			public CustomProvider() {
			String primitiveName="<xsl:value-of select="SymmetricBlockCipher/name" />";
			super(primitiveName, 1.0, primitiveName+ "v1.0");

			put("Cipher."+primitiveName, primitiveNameCipher.class.getName());
			}
			}
		</xsl:result-document>
	</xsl:template>
</xsl:stylesheet>