import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;
import java.lang.reflect.*;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.io.font.constants.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.*;
import com.itextpdf.layout.borders.*;
import com.itextpdf.kernel.font.*;
import com.itextpdf.io.image.*;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import java.lang.annotation.*;
public class ServiceDoc
{
public java.util.List<Class> createDataStructure(String packageName)
{
java.util.List<Class> classes = new ArrayList<>();
try
{
String path = packageName.replace('.', '/');
ClassLoader loader = Thread.currentThread().getContextClassLoader();
URL resource = loader.getResource(path);
if(resource==null) 
{
throw new IllegalArgumentException("Package not found: " + packageName);
}
File dir = new File(resource.toURI());
if(!dir.exists() || !dir.isDirectory())
{
throw new IllegalArgumentException("Not a directory: " + dir.getAbsolutePath());
}
listFiles(dir,classes,packageName);
}catch(Exception e)
{
e.printStackTrace();
}
return classes;
}
public void listFiles(File dir,java.util.List<Class> classes,String packageName)
{
for(File file : dir.listFiles())
{
if(file.isDirectory())
{
listFiles(file,classes,packageName+"."+file.getName());
}else
{
if(file.getName().endsWith(".class") && !file.getName().contains("$"))
{
String className = packageName+"."+file.getName().replace(".class", "");
try
{
Class c = Class.forName(className);
if(!c.isAnnotation()) 
{
classes.add(c);
}
}catch(ClassNotFoundException e)
{ 
System.out.println("class not found");
continue;
}
}
}
}
}
public static void createPdf(String fileName,java.util.List<Class> classes)
{
File file = new File(fileName);
try
{
if(file.exists()) file.delete();
PdfWriter pdfWriter = new PdfWriter(file);
PdfDocument pdfDocument = new PdfDocument(pdfWriter);
Document doc = new Document(pdfDocument);
InputStream is = ServiceDoc.class.getResourceAsStream("/logo.png");
Image logo=null;
if (is !=null)
{
ImageData imageData = ImageDataFactory.create(is.readAllBytes());
logo = new Image(imageData); 
}
Paragraph logoPara = new Paragraph();
logoPara.add(logo);
Paragraph companyNamePara = new Paragraph();
companyNamePara.add("ServiceDoc");
PdfFont companyNameFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
companyNamePara.setFont(companyNameFont);
companyNamePara.setFontSize(16);
PdfFont pageNumberFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
PdfFont columnTitleFont  = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
PdfFont dataFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
Paragraph columnTitle1 = new Paragraph("Path");
columnTitle1.setFont(columnTitleFont);
columnTitle1.setFontSize(13);
Paragraph columnTitle2 = new Paragraph("Class Name With Package");
columnTitle2.setFont(columnTitleFont);
columnTitle2.setFontSize(13);
Paragraph columnTitle3 = new Paragraph("Annotations On Class");
columnTitle3.setFont(columnTitleFont);
columnTitle3.setFontSize(13);
Paragraph pageNumberParagraph;
Paragraph dataParagraph;
float topTableColumnWidths[] ={1,5};
float dataTableColumnWidths[] ={2,7,7,7};
int sno,x,pageSize;
pageSize =1;
boolean newPage = true;
Table pageNumberTable;
Table topTable;
Table dataTable = null;
Cell cell;
Annotation annos[]=null;
int numberOfPages = classes.size()/pageSize;
if((classes.size()%pageSize)!=0) numberOfPages++;
int pageNumber =0;
sno =1;
x=0;
boolean  InjectSScope=false;
boolean  InjectAScope=false;
boolean  InjectRScope=false;
boolean  InjectAD = false;
String name=null;
if(classes.size()==0)
{
System.out.println("No classes found");
return;
}
while(x<classes.size())
{
Class c = classes.get(x);
name = c.getSimpleName();
if(newPage==true)
{
pageNumber++;
topTable = new Table(UnitValue.createPercentArray(topTableColumnWidths));
cell = new Cell();
cell.setBorder(Border.NO_BORDER);
cell.add(logoPara);
topTable.addCell(cell);
cell = new Cell();
cell.add(companyNamePara);
cell.setBorder(Border.NO_BORDER);
cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
topTable.addCell(cell);
doc.add(topTable);
pageNumberParagraph = new Paragraph("page: "+pageNumber+"/"+numberOfPages);
pageNumberParagraph.setFont(pageNumberFont);
pageNumberParagraph.setFontSize(13);
pageNumberTable = new Table(1);
pageNumberTable.setWidth(UnitValue.createPercentValue(100));
cell = new Cell();
cell.setBorder(Border.NO_BORDER);
cell.add(pageNumberParagraph);
cell.setTextAlignment(TextAlignment.RIGHT);
pageNumberTable.addCell(cell);
doc.add(pageNumberTable);
dataTable =new Table(UnitValue.createPercentArray(dataTableColumnWidths));
dataTable.setWidth(UnitValue.createPercentValue(100));
dataTable.setFixedLayout();

cell = new Cell(1,4);
Paragraph para = new Paragraph(String.valueOf(sno)+". Class Name :"+name);
para.setBold();
para.setFont(columnTitleFont);
para.setFontColor(new DeviceRgb(0, 0, 139));
para.setFontSize(14);
cell.add(para);
cell.setTextAlignment(TextAlignment.LEFT);
cell.setBorder(Border.NO_BORDER);
dataTable.addCell(cell);
Annotation aOnC[] = c.getAnnotations();
for(Annotation ano:aOnC)
{
if(ano.annotationType().getSimpleName().equals("InjectSessionScope"))
{
InjectSScope=true;
}else if(ano.annotationType().getSimpleName().equals("InjectRequestScope"))
{
InjectRScope=true;
}else if(ano.annotationType().getSimpleName().equals("InjectApplicationScope"))
{
InjectAScope=true;
}
else if(ano.annotationType().getSimpleName().equals("InjectApplicationDirectory"))
{
InjectAD = true;
}
}
if(InjectRScope)
{
cell = new Cell(1,4);
para = new Paragraph("InjectionRequestScope :true");
para.setBold();
para.setFont(columnTitleFont);
para.setFontColor(ColorConstants.DARK_GRAY);
cell.add(para);
cell.setTextAlignment(TextAlignment.LEFT);
cell.setBorder(Border.NO_BORDER);
dataTable.addCell(cell);
}
if(InjectSScope)
{
cell = new Cell(1,4);
para = new Paragraph("InjectSessionScope :true");
para.setBold();
para.setFont(columnTitleFont);
para.setFontColor(ColorConstants.DARK_GRAY);
cell.add(para);
cell.setTextAlignment(TextAlignment.LEFT);
cell.setBorder(Border.NO_BORDER);
dataTable.addCell(cell);
}
if(InjectAScope)
{
cell = new Cell(1,4);
para = new Paragraph("InjectApplicationScope :true");
para.setBold();
para.setFont(columnTitleFont);
para.setFontColor(ColorConstants.DARK_GRAY);
cell.add(para);
cell.setTextAlignment(TextAlignment.LEFT);
cell.setBorder(Border.NO_BORDER);
dataTable.addHeaderCell(cell);
}
if(InjectAD)
{
cell = new Cell(1,4);
para = new Paragraph("InjectApplicationDirectory :true");
para.setBold();
para.setFont(columnTitleFont);
para.setFontColor(ColorConstants.DARK_GRAY);
cell.add(para);
cell.setTextAlignment(TextAlignment.LEFT);
cell.setBorder(Border.NO_BORDER);
dataTable.addCell(cell);
}
cell = new Cell(1,2);
cell.add(columnTitle1);
dataTable.addCell(cell);
cell = new Cell();
cell.add(columnTitle2);
dataTable.addCell(cell);
cell = new Cell();
cell.add(columnTitle3);
dataTable.addCell(cell);
newPage = false;
}
String nameWithPackage = c.getName();
cell  = new Cell(1,2);
dataParagraph = new Paragraph("/"+name);
dataParagraph.setFont(dataFont);
dataParagraph.setFontSize(14);
cell.add(dataParagraph);
dataTable.addCell(cell);
cell  = new Cell();
nameWithPackage = nameWithPackage.replace(".", ".\u200B");
dataParagraph = new Paragraph(nameWithPackage);
dataParagraph.setMultipliedLeading(1.2f);
dataParagraph.setFont(dataFont);
dataParagraph.setFontSize(14);
cell.add(dataParagraph);
cell.setKeepTogether(false);
cell.setPadding(5);
dataTable.addCell(cell);
cell  = new Cell();
annos = c.getAnnotations();
if(annos.length==0)
{ 
dataParagraph = new Paragraph("No Annotations");
dataParagraph.setFont(dataFont);
dataParagraph.setFontSize(14);
cell.add(dataParagraph);
}
for(Annotation anno:annos)
{
String nameOfA = anno.annotationType().getSimpleName();
if(nameOfA.equals("InjectRequestScope") || nameOfA.equals("InjectApplicationScope") || nameOfA.equals("InjectSessionScope") || nameOfA.equals("InjectApplicationDirectory"))
{
dataParagraph = new Paragraph("-");
dataParagraph.setFont(dataFont);
dataParagraph.setFontSize(14);
cell.add(dataParagraph);
continue;
}
dataParagraph = new Paragraph(nameOfA);
dataParagraph.setFont(dataFont);
dataParagraph.setFontSize(14);
dataParagraph.setMultipliedLeading(1.2f);
cell.add(dataParagraph);
}
dataTable.addCell(cell);

Method methods[] = c.getDeclaredMethods();
if(methods.length>0)
{
int no =0;
for(Method m:methods)
{
no++;
cell = new Cell(1,4);
Paragraph p1 = new Paragraph(no+"."+" Method");
Paragraph p2=null;
p1.setFontColor(new DeviceRgb(0, 0, 139));
p1.setFont(columnTitleFont);
p1.setFontSize(14);
cell.add(p1);
cell.setTextAlignment(TextAlignment.LEFT);
cell.setBorder(Border.NO_BORDER);
dataTable.addCell(cell);
cell = new Cell(1,4);
p1 = new Paragraph("Return Type: "+m.getReturnType());
p1.setFontColor(new DeviceRgb(0, 0, 139));
p1.setFont(columnTitleFont);
p1.setFontSize(12);
cell.add(p1);
cell.setTextAlignment(TextAlignment.LEFT);
cell.setBorder(Border.NO_BORDER);
dataTable.addCell(cell);
cell = new Cell(1,2);
Paragraph column1 = new Paragraph("Path");
column1.setFont(columnTitleFont);
cell.add(column1);
dataTable.addCell(cell);
cell = new Cell();
Paragraph column2 = new Paragraph("Method Name");
column2.setFont(columnTitleFont);
dataTable.addCell(column2);
Paragraph column3= new Paragraph("Annotations On Method");
column3.setFont(columnTitleFont);
dataTable.addCell(column3);
String mn = m.getName();
cell  = new Cell(1,2);
dataParagraph = new Paragraph("/"+name+"/"+mn);
dataParagraph.setFont(dataFont);
dataParagraph.setFontSize(14);
dataParagraph.setMultipliedLeading(1.2f);
cell.add(dataParagraph);
cell.setPadding(5);
cell.setKeepTogether(false);
dataTable.addCell(cell);
cell  = new Cell();
dataParagraph = new Paragraph(mn);
dataParagraph.setFont(dataFont);
dataParagraph.setFontSize(14);
dataParagraph.setMultipliedLeading(1.2f);
cell.add(dataParagraph);
cell.setPadding(5);
cell.setKeepTogether(false);
dataTable.addCell(cell);
cell  = new Cell();
annos = m.getAnnotations();
if(annos.length==0)
{ 
dataParagraph = new Paragraph("No Annotations");
dataParagraph.setFont(dataFont);
dataParagraph.setFontSize(14);
cell.add(dataParagraph);
}
for(Annotation ann:annos)
{
String anoName =ann.annotationType().getSimpleName();
dataParagraph = new Paragraph(anoName);
dataParagraph.setFont(dataFont);
dataParagraph.setFontSize(14);
dataParagraph.setMultipliedLeading(1.2f);
cell.add(dataParagraph);
}
cell.setPadding(5);
cell.setKeepTogether(false);
dataTable.addCell(cell);
Parameter params[]=m.getParameters();
cell = new Cell(1,4);
p1 = new Paragraph("Method Parameters");
p1.setFont(columnTitleFont);
p1.setFontColor(new DeviceRgb(0,0, 139));
p1.setFontSize(14);
cell.add(p1);
cell.setTextAlignment(TextAlignment.LEFT);
cell.setBorder(Border.NO_BORDER);
dataTable.addCell(cell);
if(params.length>0)
{
Paragraph col1 = new Paragraph("No.");
col1.setFont(columnTitleFont);
col1.setFontSize(13);
dataTable.addCell(col1);
Paragraph col2 = new Paragraph("Name");
col2.setFont(columnTitleFont);
col2.setFontSize(13);
dataTable.addCell(col2);
Paragraph col3 = new Paragraph("Type");
col3.setFont(columnTitleFont);
col3.setFontSize(13);
dataTable.addCell(col3);
Paragraph col4= new Paragraph("Annotations On Parameter");
col4.setFont(columnTitleFont);
col4.setFontSize(13);
dataTable.addCell(col4);
int pn=0;
for(Parameter par:params)
{
Annotation aOnP[]=par.getAnnotations();
boolean rpOnP = false;
boolean requestScope = false;
boolean applicationScope =false;
boolean sessionScope = false;
for(Annotation an:aOnP)
{
if(an.annotationType().getSimpleName().equals("InjectRequestParameter"))
{
rpOnP=true;
}
if(an.annotationType().getSimpleName().equals("RequestScope"))
{
requestScope=true;
}
if(an.annotationType().getSimpleName().equals("ApplicationScope"))
{
applicationScope=true;
}
if(an.annotationType().getSimpleName().equals("SessionScope"))
{
sessionScope=true;
}
}
if(rpOnP)
{
cell = new Cell(1,4);
p1 = new Paragraph("InjectRequestParameter :true");
p1.setBold();
p1.setFont(columnTitleFont);
p1.setFontColor(ColorConstants.DARK_GRAY);
cell.add(p1);
cell.setTextAlignment(TextAlignment.LEFT);
cell.setBorder(Border.NO_BORDER);
dataTable.addCell(cell);
}
if(requestScope)
{
cell = new Cell(1,4);
p1 = new Paragraph("RequestScope :true");
p1.setBold();
p1.setFont(columnTitleFont);
p1.setFontColor(ColorConstants.DARK_GRAY);
cell.add(p1);
cell.setTextAlignment(TextAlignment.LEFT);
cell.setBorder(Border.NO_BORDER);
dataTable.addCell(cell);
}
if(sessionScope)
{
cell = new Cell(1,4);
p1 = new Paragraph("SessionScope :true");
p1.setBold();
p1.setFont(columnTitleFont);
p1.setFontColor(ColorConstants.DARK_GRAY);
cell.add(p1);
cell.setTextAlignment(TextAlignment.LEFT);
cell.setBorder(Border.NO_BORDER);
dataTable.addCell(cell);
}
if(applicationScope)
{
cell = new Cell(1,4);
p1 = new Paragraph("ApplicationScope :true");
p1.setBold();
p1.setFont(columnTitleFont);
p1.setFontColor(ColorConstants.DARK_GRAY);
cell.add(p1);
cell.setTextAlignment(TextAlignment.LEFT);
cell.setBorder(Border.NO_BORDER);
dataTable.addCell(cell);
}
pn++;
Class type = par.getType();
String pname = par.getName();
cell = new Cell();
dataParagraph  = new Paragraph(String.valueOf(pn));
dataParagraph.setFont(dataFont);
dataParagraph.setFontSize(14);
cell.add(dataParagraph);
cell.setTextAlignment(TextAlignment.RIGHT);
dataTable.addCell(cell);
cell  = new Cell();
dataParagraph = new Paragraph(pname);
dataParagraph.setFont(dataFont);
dataParagraph.setFontSize(14);
cell.add(dataParagraph);
dataTable.addCell(cell);
cell  = new Cell();
dataParagraph = new Paragraph(type.getSimpleName());
dataParagraph.setFont(dataFont);
dataParagraph.setFontSize(14);
cell.add(dataParagraph);
dataTable.addCell(cell);
cell  = new Cell();
annos = par.getAnnotations();
if(annos.length==0)
{ 
dataParagraph = new Paragraph("No Annotations");
dataParagraph.setFont(dataFont);
dataParagraph.setFontSize(14);
cell.add(dataParagraph);
}	
for(Annotation anno:annos)
{
String nameOfA = anno.annotationType().getSimpleName();
if(nameOfA.equals("InjectRequestParameter") || nameOfA.equals("ApplicationScope") || nameOfA.equals("SessionScope") || nameOfA.equals("RequestScope"))
{
dataParagraph = new Paragraph("-");
dataParagraph.setFontSize(14);
cell.add(dataParagraph);
continue;
}
dataParagraph = new Paragraph(nameOfA);
dataParagraph.setFont(dataFont);
dataParagraph.setFontSize(14);
dataParagraph.setMultipliedLeading(1.2f);
cell.add(dataParagraph);
cell.setPadding(5);
cell.setKeepTogether(false);
}
dataTable.addCell(cell);
}
}else
{
cell = new Cell(1,4);
p1 = new Paragraph("No Parameters");
p1.setFontColor(ColorConstants.DARK_GRAY);
p1.setFont(columnTitleFont);
p1.setFontSize(13);
cell.add(p1);
cell.setTextAlignment(TextAlignment.LEFT);
cell.setBorder(Border.NO_BORDER);
dataTable.addCell(cell);
}
}
}else
{
cell = new Cell(1,4);
Paragraph para = new Paragraph("No Methods");
para.setFontColor(ColorConstants.DARK_GRAY);
para.setFont(columnTitleFont);
para.setFontSize(13);
cell.add(para);
cell.setTextAlignment(TextAlignment.LEFT);
cell.setBorder(Border.NO_BORDER);
dataTable.addCell(cell);
}//else 
sno++;
x++;
if(sno%pageSize==0 || x==classes.size())
{
doc.add(dataTable);
if(x<classes.size())
{
doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
newPage = true;
}
}
}
doc.close();
System.out.println("Pdf generated "+file.getAbsolutePath());
}catch(Exception exception)
{
exception.printStackTrace();
}
}
public static void main(String args[])
{
try
{
String packageName =args[0];
String pdfName = args[1];
ServiceDoc sd = new ServiceDoc();
java.util.List classes = sd.createDataStructure(packageName);
sd.createPdf(pdfName,classes);
}catch(ArrayIndexOutOfBoundsException aiobe)
{
System.out.println("Invalid argument");
System.out.println("use as java -cp <classpath of package> ServiceDoc <package name> <pdfName>");
}
}
}
