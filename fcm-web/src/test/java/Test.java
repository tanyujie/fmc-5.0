import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.sinhy.excel.ExcelUtils;
import com.sinhy.excel.ExcelWriterContext;
import com.sinhy.excel.SheetWriter;

/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : Test.java
 * Package     : 
 * @author lilinhai 
 * @since 2020-12-24 17:21
 * @version  V1.0
 */

/**  
 * <pre>请加上该类的描述</pre>
 * @author lilinhai
 * @since 2020-12-24 17:21
 * @version V1.0  
 */
public class Test
{
    public static void main(String[] args) throws Exception
    {
        FileOutputStream outputStream=new FileOutputStream("D:\\test.xlsx");
        ExcelWriterContext excelWriterContext = ExcelUtils.create(outputStream);
        SheetWriter writer = excelWriterContext.create("测试");
        writer.setHeaders("1");
        writer.addRowData(1,"哈哈");
        excelWriterContext.close();
    }
}
