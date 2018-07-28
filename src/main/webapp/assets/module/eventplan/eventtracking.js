window.onFocus = function () {
    ins.loadActivities();
}

let ins = new Vue({
    el: '#app',
    data: {},
    data() {
        return {
            publish:{
                visible:false,
                contentId:19
            },
            costPlanData: [],
            costPlanDataDialog: {
                from: {},
                update: false,
                visiable: false
            },
            curPlanData: null,
            planDataVE: {
                title: '',
                display: false,
                mode: 'view',
                content: ''
            },
            dis_h_v: false,
            tpager: {
                total: 0,
                current: 1,
                size: 10
            },
            orgUsers: [], //组织用户
            activities: [],
            activity_status: [{
                    "status": -1,
                    "name": "未通过"
                },
                {
                    "status": 0,
                    "name": "计划已就绪"
                },
                {
                    "status": 1,
                    "name": "正在投票"
                },
                {
                    "status": 3,
                    "name": "投票通过"
                },
                {
                    "status": 4,
                    "name": "等待开始"
                },
                {
                    "status": 5,
                    "name": "正在进行"
                },
                {
                    "status": 6,
                    "name": "结束"
                }
            ],
            costTypeGroup: [],
            votingResult:{
                yesCount:0,
                total:1,
                data:[]
            },
            votingDetail:{
                visible:false,
                datas:[]
            }
        }
    },
    mounted() {
        this.loadActivities();
        this.loadOrgUsers();
    },
    methods: {
        showPubState(data){
            this.publish.visible = true;
            this.publish.contentId = data.pubTaskId
        },
        //新建花费
        newCostPlan(item) {
            this.costPlanData = [{}];
            this.costPlanDataDialog.from = item;
            this.costPlanDataDialog.mode = 'edit';
            this.costPlanDataDialog.update = false;
            this.costPlanDataDialog.visiable = true;
        },
        viewCostPlan(item) {
            this.costPlanData = item.costplans;
            this.costPlanDataDialog.from = item;
            this.costPlanDataDialog.mode = 'view';
            this.costPlanDataDialog.visiable = true;
        },
        editCostPlan(item) {
            this.costPlanData = item.costplans;
            this.costPlanDataDialog.from = item;
            this.costPlanDataDialog.mode = 'edit';
            this.costPlanDataDialog.update = true;
            this.costPlanDataDialog.visiable = true;
        },
        //计划更新
        costPlanDataComplete(planDatas) {
            if (!planDatas || planDatas.length == 0) return;
            let from = this.costPlanDataDialog.from;
            planDatas.forEach(el => {
                el.eventPlanId = from.eventPlanId
            });
            let url = '/costplan/plan';
            if (this.costPlanDataDialog.update) {
                ajax_json_promise(url, 'put', planDatas).then(result => {
                    $message('更新成功', 'success', this);
                    this.costPlanDataDialog.visiable = false
                    this.loadActivities();
                });
            }
            else{
                ajax_json_promise(url,'post',planDatas).then(result=>{
                    $message('更新成功', 'success', this);
                    this.costPlanDataDialog.visiable = false;
                    this.loadActivities();
                });
            }
        },
        viewPlan(item) {
            this.planDataVE.title = item.title;
            this.planDataVE.mode = 'view';
            this.planDataVE.content = item.content;
            this.planDataVE.display = true;
        },
        editPlan(item) {
            this.planDataVE.title = item.title;
            this.planDataVE.mode = 'edit';
            this.planDataVE.content = item.content;
            this.planDataVE.display = true;
            this.curPlanData = item;
        },
        getStatusType(item) {
            if (item.status == -1) return 'danger';
            if (item.status == 6) return '';
            if (item.status > 3 && item.status < 5) return 'success';
            return 'info';
        },
        getStatusName(item) {
            return this.activity_status.filter(u => u.status == item.status).map(i => i.name).join('');
        },
        getUserName(item) {
            return this.orgUsers.filter(u => u.userId == item.userId).map(i => i.username).join('');
        },
        loadOrgUsers() {
            let url = `/sys/user/querySysUsersNotPage`;
            ajax_json_promise(url, 'post', {}).then(result => {
                this.orgUsers = result.data;
            });
        },
        loadActivities() {
            let pageIndex = this.tpager.current;
            let limit = this.tpager.size;
            let url = `/event/plan/${pageIndex}-${limit}`;
            ajax_json_promise(url, 'post', {}).then(result => {
                this.activities = result.data;
                this.setPagerInfo(result.pager);
            });
        },
        planDataSubmit(content) {
            console.debug('计划数据 ', content);
            this.curPlanData.content = content;
            let url = '/event/plan';
            ajax_json_promise(url, 'put', this.curPlanData).then(result => {
                this.curPlanData = null;
                this.loadActivities();
            });
        },
        setPagerInfo(pager) {
            this.tpager.total = pager.total;
            this.tpager.current = pager.pageIndex;
        },
        handleSizeChange(val) {
            //console.log(`每页 ${val} 条`);
            this.tpager.size = val;
        },
        handleCurrentChange(val) {
            //console.log(`当前页: ${val}`);
            this.tpager.current = val;
        },
        //加载投票信息
        showVotingInfo(dt){
            let eventPlanId = dt.eventPlanId;
            let url = `/event/voting/${eventPlanId}`;
            ajax_promise(url,'get',{}).then(result=>{
                this.votingResult = result.data;
            });
        },
        votingDetailView(){
            this.votingDetail.visible = true;
        },
        votingSummary(){
            const sums = [];
            sums[0] = "赞成: " + this.votingResult.yesCount + " 票";
            sums[1] = "反对: " + this.votingResult.noCount + " 票";
            sums[2] = "总共: " + this.votingResult.total + " 票";
            return sums;
        },//投屏任务创建 
        createPubTask(data){
            let eventPlanId = data.eventPlanId;
            let url = `/view/publish/new.jsp?eventPlanId=${eventPlanId}`
            let name = '创建投屏任务';
            if(top.addTab){
                let obj = {menuId:'pubTask_${eventPlanId}',name:name,url:url,closable:true,icon:''};
                top.addTab(obj);
            }else{
                openwindow(url,name,800,600);
            }
        }
    }
});

window.appInstince = ins;