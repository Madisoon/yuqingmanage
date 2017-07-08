<?xml version="1.0"?>
<?mso-application progid="Excel.Sheet"?>
<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:o="urn:schemas-microsoft-com:office:office"
 xmlns:x="urn:schemas-microsoft-com:office:excel"
 xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:html="http://www.w3.org/TR/REC-html40">
 <DocumentProperties xmlns="urn:schemas-microsoft-com:office:office">
  <Created>2006-09-16T00:00:00Z</Created>
  <LastSaved>2017-04-13T08:57:57Z</LastSaved>
  <Version>15.00</Version>
 </DocumentProperties>
 <OfficeDocumentSettings xmlns="urn:schemas-microsoft-com:office:office">
  <AllowPNG/>
  <RemovePersonalInformation/>
 </OfficeDocumentSettings>
 <ExcelWorkbook xmlns="urn:schemas-microsoft-com:office:excel">
  <WindowHeight>8010</WindowHeight>
  <WindowWidth>14805</WindowWidth>
  <WindowTopX>240</WindowTopX>
  <WindowTopY>105</WindowTopY>
  <ProtectStructure>False</ProtectStructure>
  <ProtectWindows>False</ProtectWindows>
 </ExcelWorkbook>
 <Styles>
  <Style ss:ID="Default" ss:Name="Normal">
   <Alignment ss:Vertical="Bottom"/>
   <Borders/>
   <Font ss:FontName="宋体" x:CharSet="134" ss:Size="11" ss:Color="#000000"/>
   <Interior/>
   <NumberFormat/>
   <Protection/>
  </Style>
  <Style ss:ID="s77" ss:Name="超链接">
   <Font ss:FontName="宋体" x:CharSet="134" ss:Size="11" ss:Color="#0000FF"
    ss:Underline="Single"/>
  </Style>
  <Style ss:ID="s112">
   <Alignment ss:Horizontal="Center" ss:Vertical="Bottom"/>
   <Font ss:FontName="微软雅黑" x:CharSet="134" x:Family="Swiss" ss:Size="11"
    ss:Color="#000000"/>
   <NumberFormat ss:Format="@"/>
  </Style>
  <Style ss:ID="s113" ss:Parent="s77">
   <Alignment ss:Horizontal="Center" ss:Vertical="Bottom"/>
   <Font ss:FontName="微软雅黑" x:CharSet="134" x:Family="Swiss" ss:Size="11"
    ss:Color="#0000FF" ss:Underline="Single"/>
   <NumberFormat ss:Format="@"/>
  </Style>
  <Style ss:ID="s124">
   <Alignment ss:Horizontal="Center" ss:Vertical="Bottom"/>
   <Font ss:FontName="微软雅黑" x:CharSet="134" x:Family="Swiss" ss:Size="11"
    ss:Color="#000000" ss:Bold="1"/>
  </Style>
 </Styles>
 <Worksheet ss:Name="Sheet1">
  <Table ss:ExpandedColumnCount="6" ss:ExpandedRowCount="10000000" x:FullColumns="1"
   x:FullRows="1" ss:DefaultColumnWidth="54" ss:DefaultRowHeight="13.5">
   <Column ss:Index="2" ss:AutoFitWidth="0" ss:Width="186.75"/>
   <Column ss:AutoFitWidth="0" ss:Width="241.5"/>
   <Column ss:AutoFitWidth="0" ss:Width="338.25"/>
   <Column ss:AutoFitWidth="0" ss:Width="59.25"/>
   <Column ss:AutoFitWidth="0" ss:Width="75.75"/>
   <Row ss:Height="15">
    <Cell ss:StyleID="s124"><Data ss:Type="String">编号</Data></Cell>
    <Cell ss:StyleID="s124"><Data ss:Type="String">信息标题</Data></Cell>
    <Cell ss:StyleID="s124"><Data ss:Type="String">信息内容</Data></Cell>
    <Cell ss:StyleID="s124"><Data ss:Type="String">信息链接</Data></Cell>
    <Cell ss:StyleID="s124"><Data ss:Type="String">信息来源</Data></Cell>
    <Cell ss:StyleID="s124"><Data ss:Type="String">发布时间</Data></Cell>
   </Row>
      <#list inforList as infor>
   <Row ss:Height="16.5">
    <Cell ss:StyleID="s112"><Data ss:Type="Number">${infor.index}</Data></Cell>
    <Cell ss:StyleID="s112"><Data ss:Type="String">${infor.title}</Data></Cell>
    <Cell ss:StyleID="s112"><Data ss:Type="String">${infor.context}</Data></Cell>
    <Cell ss:StyleID="s112"><Data ss:Type="String">${infor.link}</Data></Cell>
    <Cell ss:StyleID="s112"><Data ss:Type="String">${infor.source}</Data></Cell>
    <Cell ss:StyleID="s112"><Data ss:Type="String">${infor.time}</Data></Cell>
   </Row>
      </#list>
  </Table>
  <WorksheetOptions xmlns="urn:schemas-microsoft-com:office:excel">
   <PageSetup>
    <Header x:Margin="0.3"/>
    <Footer x:Margin="0.3"/>
    <PageMargins x:Bottom="0.75" x:Left="0.7" x:Right="0.7" x:Top="0.75"/>
   </PageSetup>
   <Print>
    <ValidPrinterInfo/>
    <PaperSizeIndex>9</PaperSizeIndex>
    <HorizontalResolution>200</HorizontalResolution>
    <VerticalResolution>200</VerticalResolution>
    <NumberofCopies>0</NumberofCopies>
   </Print>
   <Selected/>
   <Panes>
    <Pane>
     <Number>3</Number>
     <ActiveRow>2</ActiveRow>
    </Pane>
   </Panes>
   <ProtectObjects>False</ProtectObjects>
   <ProtectScenarios>False</ProtectScenarios>
  </WorksheetOptions>
 </Worksheet>
</Workbook>
