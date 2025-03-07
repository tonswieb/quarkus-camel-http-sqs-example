<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs">
    
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>
    
    <!-- Variable to store item totals -->
    <xsl:variable name="itemTotals">
        <xsl:for-each select="//item">
            <total>
                <xsl:value-of select="xs:decimal(price) * xs:decimal(quantity)"/>
            </total>
        </xsl:for-each>
    </xsl:variable>
    
    <xsl:template match="/order">
        <processedOrder>
            <orderReference>
                <xsl:value-of select="orderId"/>
            </orderReference>
            <customerDetails>
                <customerName>
                    <xsl:value-of select="customer/name"/>
                </customerName>
                <customerEmail>
                    <xsl:value-of select="customer/email"/>
                </customerEmail>
            </customerDetails>
            <orderItems>
                <xsl:for-each select="items/item">
                    <orderItem>
                        <sku>
                            <xsl:value-of select="productId"/>
                        </sku>
                        <orderedQuantity>
                            <xsl:value-of select="quantity"/>
                        </orderedQuantity>
                        <unitPrice>
                            <xsl:value-of select="format-number(xs:decimal(price), '0.00')"/>
                        </unitPrice>
                        <totalPrice>
                            <xsl:value-of select="format-number(xs:decimal(price) * xs:decimal(quantity), '0.00')"/>
                        </totalPrice>
                    </orderItem>
                </xsl:for-each>
            </orderItems>
            <totalOrderValue>
                <xsl:value-of select="format-number(sum($itemTotals/total), '0.00')"/>
            </totalOrderValue>
        </processedOrder>
    </xsl:template>
</xsl:stylesheet> 