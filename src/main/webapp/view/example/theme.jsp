<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>vcharts主题</title>
<%@include file="/include/head.jsp"%>
<%@include file="/include/vcharts.jsp"%>

    <script src="/assets/vcharts/essos.js"></script>
    <script src="/assets/vcharts/infographic.js"></script>
    <script src="/assets/vcharts/macarons.js"></script>
    <script src="/assets/vcharts/roma.js"></script>
    <script src="/assets/vcharts/shine.js"></script>
    <script src="/assets/vcharts/walden.js"></script>
    <script src="/assets/vcharts/customed.js"></script>
</head>

<body>
    <div id="app" style="padding:5px;">
        <h1>图表主题</h1>
        <el-row>
            <el-col :span="24" style="margin-bottom:5px;">
                更改主题:
                <el-button-group>
                    <template v-for="tn in themeNames">
                        <el-button icon="el-icon-edit" @click="chartConfig.themeName=tn">{{tn}}</el-button>
                    </template>
                </el-button-group>
            </el-col>
        </el-row>
        <el-row :gutter="20">
            <el-col :span="12">
                <ve-line :theme-name="chartConfig.themeName" :title="chartData.title" :data="chartData" :toolbox="chartConfig.toolbox"></ve-line>
            </el-col>
            <el-col :span="12">
                <ve-line :theme-name="chartConfig.themeName" :title="line_area_Data.title" :data="line_area_Data" :settings="line_area_Data.chartSettings"
                    :toolbox="chartConfig.toolbox"></ve-line>
            </el-col>
        </el-row>
        <el-row :gutter="20">
            <el-col :span="12">
                <ve-histogram :theme-name="chartConfig.themeName" :title="histogramData.title" :data="histogramData" :toolbox="chartData.toolbox"></ve-histogram>
            </el-col>
            <el-col :span="12">
                <ve-histogram :theme-name="chartConfig.themeName" :title="hd2.title" :data="hd2" :settings="hd2.chartSettings" :toolbox="chartConfig.toolbox"></ve-histogram>
            </el-col>
        </el-row>
        <el-row :gutter="20">
            <el-col :span="12">
                <ve-pie :theme-name="chartConfig.themeName" :title="pie_chart1.title" :data="pie_chart1" :toolbox="chartConfig.toolbox"></ve-pie>
            </el-col>
            <el-col :span="12">
                <ve-pie :theme-name="chartConfig.themeName" :title="pie_chart2.title" :data="pie_chart2" :settings="pie_chart2.chartSettings" :toolbox="chartConfig.toolbox"></ve-pie>
            </el-col>
        </el-row>
        <el-row :gutter="20">
            <el-col :span="12">
                <ve-pie :theme-name="chartConfig.themeName" :title="pie_chart3.title" :data="pie_chart3" :settings="pie_chart3.chartSettings" :toolbox="chartConfig.toolbox"></ve-pie>
            </el-col>
            <el-col :span="12">
                <ve-ring :theme-name="chartConfig.themeName" :title="ring_chart.title" :data="ring_chart" :toolbox="chartConfig.toolbox"></ve-ring>
            </el-col>
        </el-row>
    </div>

</body>

<script type="module">
    //导入vchart 公共配置信息
    import chartConfig from '/assets/js/vcharts_config.js';
    var ins = new Vue({
        el: '#app',
        data: {
            chartConfig:chartConfig,
            themeNames: ['shine', 'essos', 'walden', 'infographic', 'macarons', 'roma', 'customed'],
            ring_chart: {
                title: {
                    text: '环图'
                },
                columns: ['日期', '访问用户'],
                rows: [{
                        '日期': '1/1',
                        '访问用户': 1393
                    },
                    {
                        '日期': '1/2',
                        '访问用户': 3530
                    },
                    {
                        '日期': '1/3',
                        '访问用户': 2923
                    },
                    {
                        '日期': '1/4',
                        '访问用户': 1723
                    },
                    {
                        '日期': '1/5',
                        '访问用户': 3792
                    },
                    {
                        '日期': '1/6',
                        '访问用户': 4593
                    }
                ]
            },
            pie_chart3: {
                title: {
                    text: '多圆饼图'
                },
                chartSettings: {
                    level: [
                        ['1/1', '1/2', '1/3'],
                        ['1/4', '1/5']
                    ]
                },
                columns: ['日期', '访问用户'],
                rows: [{
                        '日期': '1/1',
                        '访问用户': 1393
                    },
                    {
                        '日期': '1/2',
                        '访问用户': 3530
                    },
                    {
                        '日期': '1/3',
                        '访问用户': 2923
                    },
                    {
                        '日期': '1/4',
                        '访问用户': 1723
                    },
                    {
                        '日期': '1/5',
                        '访问用户': 3792
                    },
                    {
                        '日期': '1/6',
                        '访问用户': 4593
                    }
                ]
            },
            pie_chart2: {
                title: {
                    text: '玫瑰图'
                },
                chartSettings: {
                    roseType: 'radius'
                },
                columns: ['日期', '访问用户'],
                rows: [{
                        '日期': '1/1',
                        '访问用户': 1393
                    },
                    {
                        '日期': '1/2',
                        '访问用户': 3530
                    },
                    {
                        '日期': '1/3',
                        '访问用户': 2923
                    },
                    {
                        '日期': '1/4',
                        '访问用户': 1723
                    },
                    {
                        '日期': '1/5',
                        '访问用户': 3792
                    },
                    {
                        '日期': '1/6',
                        '访问用户': 4593
                    }
                ]
            },
            pie_chart1: {
                title: {
                    text: '饼图'
                },
                columns: ['日期', '访问用户'],
                rows: [{
                        '日期': '1/1',
                        '访问用户': 1393
                    },
                    {
                        '日期': '1/2',
                        '访问用户': 3530
                    },
                    {
                        '日期': '1/3',
                        '访问用户': 2923
                    },
                    {
                        '日期': '1/4',
                        '访问用户': 1723
                    },
                    {
                        '日期': '1/5',
                        '访问用户': 3792
                    },
                    {
                        '日期': '1/6',
                        '访问用户': 4593
                    }
                ]
            },
            line_area_Data: {
                title: {
                    text: '面积图'
                },
                chartSettings: {
                    stack: {
                        '用户': ['访问用户', '下单用户']
                    },
                    area: true
                },
                columns: ['日期', '访问用户', '下单用户', '下单率'],
                rows: [{
                        '日期': '1/1',
                        '访问用户': 1393,
                        '下单用户': 1093,
                        '下单率': 0.32
                    },
                    {
                        '日期': '1/2',
                        '访问用户': 3530,
                        '下单用户': 3230,
                        '下单率': 0.26
                    },
                    {
                        '日期': '1/3',
                        '访问用户': 2923,
                        '下单用户': 2623,
                        '下单率': 0.76
                    },
                    {
                        '日期': '1/4',
                        '访问用户': 1723,
                        '下单用户': 1423,
                        '下单率': 0.49
                    },
                    {
                        '日期': '1/5',
                        '访问用户': 3792,
                        '下单用户': 3492,
                        '下单率': 0.323
                    },
                    {
                        '日期': '1/6',
                        '访问用户': 4593,
                        '下单用户': 4293,
                        '下单率': 0.78
                    }
                ]
            },
            chartData: {
                title: {
                    text: '折线图'
                },
                //定义toolbox 配置,并在 图表组件中引用此配置
                toolbox: {
                    show: true,
                    feature: {
                        dataZoom: {
                            yAxisIndex: 'none'
                        },
                        dataView: {
                            readOnly: false
                        },
                        magicType: {
                            type: ['line', 'bar']
                        },
                        restore: {},
                        saveAsImage: {}
                    }
                },
                columns: ['日期', '访问用户', '下单用户', '下单率'],
                rows: [{
                        '日期': '1/1',
                        '访问用户': 1393,
                        '下单用户': 1093,
                        '下单率': 99.32
                    },
                    {
                        '日期': '1/2',
                        '访问用户': 3530,
                        '下单用户': 3230,
                        '下单率': 12.26
                    },
                    {
                        '日期': '1/3',
                        '访问用户': 2923,
                        '下单用户': 2623,
                        '下单率': 34.76
                    },
                    {
                        '日期': '1/4',
                        '访问用户': 1723,
                        '下单用户': 1423,
                        '下单率': 5.49
                    },
                    {
                        '日期': '1/5',
                        '访问用户': 3792,
                        '下单用户': 3492,
                        '下单率': 2.323
                    },
                    {
                        '日期': '1/6',
                        '访问用户': 4593,
                        '下单用户': 4293,
                        '下单率': 4.78
                    }
                ]
            },
            hd2: {
                title: {
                    text: '堆叠图'
                },
                chartSettings: {
                    stack: {
                        '用户': ['访问用户', '下单用户']
                    }
                },
                columns: ['日期', '访问用户', '下单用户', '下单率'],
                rows: [{
                        '日期': '1/1',
                        '访问用户': 1393,
                        '下单用户': 1093,
                        '下单率': 0.32
                    },
                    {
                        '日期': '1/2',
                        '访问用户': 3530,
                        '下单用户': 3230,
                        '下单率': 0.26
                    },
                    {
                        '日期': '1/3',
                        '访问用户': 2923,
                        '下单用户': 2623,
                        '下单率': 0.76
                    },
                    {
                        '日期': '1/4',
                        '访问用户': 1723,
                        '下单用户': 1423,
                        '下单率': 0.49
                    },
                    {
                        '日期': '1/5',
                        '访问用户': 3792,
                        '下单用户': 3492,
                        '下单率': 0.323
                    },
                    {
                        '日期': '1/6',
                        '访问用户': 4593,
                        '下单用户': 4293,
                        '下单率': 0.78
                    }
                ]
            },
            histogramData: {
                title: {
                    text: '柱状图'
                },
                columns: ['日期', '访问用户', '下单用户', '下单率'],
                rows: [{
                        '日期': '1/1',
                        '访问用户': 1393,
                        '下单用户': 1093,
                        '下单率': 0.32
                    },
                    {
                        '日期': '1/2',
                        '访问用户': 3530,
                        '下单用户': 3230,
                        '下单率': 0.26
                    },
                    {
                        '日期': '1/3',
                        '访问用户': 2923,
                        '下单用户': 2623,
                        '下单率': 0.76
                    },
                    {
                        '日期': '1/4',
                        '访问用户': 1723,
                        '下单用户': 1423,
                        '下单率': 0.49
                    },
                    {
                        '日期': '1/5',
                        '访问用户': 3792,
                        '下单用户': 3492,
                        '下单率': 0.323
                    },
                    {
                        '日期': '1/6',
                        '访问用户': 4593,
                        '下单用户': 4293,
                        '下单率': 0.78
                    }
                ]
            }
        },
        methods: {}
    });
</script>

</html>