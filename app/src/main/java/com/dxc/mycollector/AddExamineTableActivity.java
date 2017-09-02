package com.dxc.mycollector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.wjk.tableview.TableView;
import com.wjk.tableview.common.TableCellData;
import com.wjk.tableview.common.TableHeaderColumnModel;
import com.wjk.tableview.toolkits.SimpleTableDataAdapter;
import com.wjk.tableview.toolkits.SimpleTableHeaderAdapter;

public class AddExamineTableActivity extends AppCompatActivity {
    private TableView tableView;
    private Map<Integer, Pair<String, Integer>> columns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_examine_table);
        tableView = (TableView) findViewById(R.id.tableview);

        initData();

        SimpleTableDataAdapter dataAdapter = new SimpleTableDataAdapter(this, getTableData(), 6);
        dataAdapter.setTextSize(12);

        TableHeaderColumnModel columnModel = new TableHeaderColumnModel(columns);
        SimpleTableHeaderAdapter headerAdapter = new SimpleTableHeaderAdapter(this, columnModel);

        headerAdapter.setTextSize(14);

        tableView.setTableAdapter(headerAdapter, dataAdapter);

        tableView.setHeaderElevation(18);
    }

    private void initData() {
        columns = new LinkedHashMap<>();
        columns.put(0, new Pair<>("序号", 1));
        columns.put(1, new Pair<>("检查项目", 1));
        columns.put(2, new Pair<>("检查结果（合格/不合格）", 1));
    }

    private List<TableCellData> getTableData() {
        List<TableCellData> cellDatas = new ArrayList<>();
        //第0行
        cellDatas.add(new TableCellData("1", 0, 0));
        cellDatas.add(new TableCellData("班前安全讲话", 0, 1, 1, 2));
        cellDatas.add(new TableCellData("", 0, 3));
        //第1行
        cellDatas.add(new TableCellData("2", 1, 0));
        cellDatas.add(new TableCellData("作业人员防护用品佩戴情况", 1, 1, 1, 2));
        cellDatas.add(new TableCellData("", 1, 3));
        //第2行
        cellDatas.add(new TableCellData("3", 2, 0));
        cellDatas.add(new TableCellData("开挖工作面安全状态", 2, 1, 4, 1));
        cellDatas.add(new TableCellData("工作面光照度", 2, 2));
        cellDatas.add(new TableCellData("", 2, 3));
        //第3 行
        cellDatas.add(new TableCellData("4", 3, 0));
        //cellDatas.add(new TableCellData("", 3, 1, 4, 1));
        cellDatas.add(new TableCellData("通风、降尘效果", 3, 2));
        cellDatas.add(new TableCellData("", 3, 3));
        //第4 行
        cellDatas.add(new TableCellData("5", 4, 0));
        //cellDatas.add(new TableCellData("", 4, 1, 4, 1));
        cellDatas.add(new TableCellData("道路状况", 4, 2));
        cellDatas.add(new TableCellData("", 4, 3));
        //第5 行
        cellDatas.add(new TableCellData("6", 5, 0));
        //cellDatas.add(new TableCellData("", 5, 1, 4, 1));
        cellDatas.add(new TableCellData("有害气体监测情况", 5, 2));
        cellDatas.add(new TableCellData("", 5, 3));
//第6行
        cellDatas.add(new TableCellData("7", 6, 0));
        cellDatas.add(new TableCellData("湿式钻孔，严禁在残孔中继续钻孔", 6, 1, 1, 2));
        cellDatas.add(new TableCellData("", 6, 3));

//第7行
        cellDatas.add(new TableCellData("8", 7, 0));
        cellDatas.add(new TableCellData("湿式钻孔，严禁在残孔中继续钻孔", 7, 1, 1, 2));
        cellDatas.add(new TableCellData("", 7, 3));
        //第8行
        cellDatas.add(new TableCellData("9", 8, 0));
        cellDatas.add(new TableCellData("台车钻孔", 8, 1, 4, 1));
        cellDatas.add(new TableCellData("凿岩台车工作前的机况检查", 8, 2));
        cellDatas.add(new TableCellData("", 8, 3));
        //第9 行
        cellDatas.add(new TableCellData("10", 9, 0));
        //cellDatas.add(new TableCellData("", 3, 1, 4, 1));
        cellDatas.add(new TableCellData("凿岩台车行走安全", 9, 2));
        cellDatas.add(new TableCellData("", 9, 3));
        //第10 行
        cellDatas.add(new TableCellData("11", 10, 0));
        //cellDatas.add(new TableCellData("", 4, 1, 4, 1));
        cellDatas.add(new TableCellData("凿岩台车停放场所", 10, 2));
        cellDatas.add(new TableCellData("", 10, 3));
        //第11行
        cellDatas.add(new TableCellData("12", 11, 0));
        //cellDatas.add(new TableCellData("", 5, 1, 4, 1));
        cellDatas.add(new TableCellData("凿岩台车重要部位防护", 11, 2));
        cellDatas.add(new TableCellData("", 11, 3));
        //第12 行
        cellDatas.add(new TableCellData("13", 12, 0));
        cellDatas.add(new TableCellData("装药作业", 12, 1, 5, 1));
        cellDatas.add(new TableCellData("作业场所安全状态", 12, 2));
        cellDatas.add(new TableCellData("", 12, 3));
//第13行
        cellDatas.add(new TableCellData("14", 13, 0));
        cellDatas.add(new TableCellData("作业人员不得带火源，不得穿化纤衣物、炮棍材质", 13, 2));
        cellDatas.add(new TableCellData("", 13, 3));
//第14行
        cellDatas.add(new TableCellData("15", 14, 0));
        cellDatas.add(new TableCellData("采用电起爆时，散杂电流、漏泄电流的测定情况", 14, 2));
        cellDatas.add(new TableCellData("", 14, 3));

//第15行
        cellDatas.add(new TableCellData("16", 15, 0));
        cellDatas.add(new TableCellData("严禁装药与钻孔平行作业", 15, 2));
        cellDatas.add(new TableCellData("", 15, 3));
        //第16行
        cellDatas.add(new TableCellData("17", 16, 0));
        cellDatas.add(new TableCellData("装药完成后对火工品清处理", 16, 2));
        cellDatas.add(new TableCellData("", 16, 3));
        //第17行
        cellDatas.add(new TableCellData("18", 17, 0));
        cellDatas.add(new TableCellData("爆破作业", 17, 1, 2, 1));
        cellDatas.add(new TableCellData("爆破专业人员配置", 17, 2));
        cellDatas.add(new TableCellData("", 17, 3));
//第18行
        cellDatas.add(new TableCellData("19", 18, 0));
        cellDatas.add(new TableCellData("人员撤离、警戒工作", 18, 2));
        cellDatas.add(new TableCellData("", 18, 3));
        return cellDatas;
    }
}
