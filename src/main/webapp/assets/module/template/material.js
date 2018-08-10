import serverConfig from '/environment/resourceUploadConfig.jsp';

window.onclose = function () {
  let tp = appInstince.tp.data;
  if (tp.title || _editor.hasContents()) {
    return confirm('有未保存数据，是否关闭？');
  }
  return true;
}

window.onFocus = function(){
  console.debug(' 素材管理页面获取得焦点');
  
  ins.loadTpTypeData(ins.currentCategory);
  ins.loadTreeData();
}

let ins = new Vue({
  el: "#app",
  data: {
    resourceView: {
      title: '',
      visible: false
    },
    //右键 节点的数据
    curContextData: null,
    contextMenu: {
      visiable: false,
      event: null
    },
    //菜单数据
    contextMenuData: window.menuData,
    resource_server_url: '',
    resource_menu: [{
      label: '素材管理',
      children: [{
        label: '视频(8)'
      }, {
        label: '音频(4)'
      }, {
        label: '图片(34)'
      }]
    }],
    keyword: '',
    //当前目录
    currentCategory: {
      name: null,
      parent:0
    },
    tpager: {
      total: 0,
      current: 1,
      size: 10
    },
    //节目模版选择
    ptw: {
      visiable: false
    },
    rules: {
      notEmpty: [{
        required: true,
        message: "此项数据不能为空!",
        trigger: "blur"
      }],
      programTemplate: [{
        required: true,
        message: "请选择节目模版",
        trigger: "blur"
      }],
      programTemplateName: [{
        required: true,
        message: "请选择节目模版",
        trigger: "blur"
      }],
      name: [{
        required: true,
        message: "请输入名称",
        trigger: "blur"
      },
      {
        min: 1,
        max: 50,
        message: "长度在 1 到 50个字符",
        trigger: "blur"
      }
      ],
      remark: [{
        required: true,
        message: "请输入表述",
        trigger: "blur"
      },
      {
        min: 1,
        max: 50,
        message: "长度在 1 到 50个字符",
        trigger: "blur"
      }
      ],
      orderNum: [{
        required: true,
        message: "请输入排序序号",
        trigger: "blur"
      }],
      title: [{
        required: true,
        message: "请输入标题",
        trigger: "blur"
      },
      {
        min: 1,
        max: 50,
        message: "长度在 1 到 50个字符",
        trigger: "blur"
      }
      ],
      albumIds: [{
        required: true,
        message: "请选择分类",
        trigger: "change"
      }]
    },
    tpt_props: {
      label: "name",
      value: "albumId",
      children: 'children'
    },
    tp_empty: {
      previewPicture: ''
    },
    tp: {
      title: "文章模板编辑",
      visible: false,
      update: false,
      m3Visible: true,
      data: {
        title: "",
        content: "",
        albumId: '',
        albumIds: [],
        url: '',
        type: '',
        description: '',
        viewUrl: '',
        typeDisable: false,
        coverUrl: '',
        pageLink: ''
      }
    },
    tpt: {
      title: "测试标题",
      visible: false,
      update: false, // 是否是更新
      data: {
        tpTypeId: "",
        name: "",
        remark: "",
        orderNum: 0,
        parent: 0
      }
    },
    importResource: {
      title: "导入资源",
      visiable: false,
      nameType: 'fileName',
      commitOnUpload: false,
      fileList: [],
      data: {
        albumIds: []
      }
    },
    currentDate: new Date(),
    props: {
      children: 'children',
      label(data, node) {
        let label = data.data.name;
        if (data.data.count) {
          label += '(' + data.data.count + ')';
        }
        return label;
      }
    },
    tpt_data: [],
    tps: [],
    tpt_data_normal: [],
    beforeDelete:{},
    storeInfo:{}
  },
  mounted() {
    this.loadTreeData();
    this.loadTpTypeData(null, this);
    this.initM3();
    this.loadStoreInfo();
  },
  computed: {
    nextDirs(){
      let ay = [];
      if(!this.currentCategory.name){
        ay = ay = this.tpt_data;
      }else{
        let nextData = getDataNextItem(this.currentCategory, this.tpt_data, item => item.children, item => item.albumId, item => item.data);
        if(nextData){
          ay = nextData.children;
        }
      }
      return ay;
    },
    loadOrgUsers() {
      let orgId = window.sysInfo.orgId;
      let url = `/sys/user/querySysUsersProgram`;
      ajax_json_promise(url, 'post', { orgId: orgId }).then(result => {
        orgUsers = result.data;
      });
    },
    breadcrumbData() {
      if(!this.currentCategory.albumId)return[];
      let bp = breadPath(this.currentCategory, this.tpt_data, item => item.children, item => item.parent, item => item.albumId, item => item.data);
      return bp;
    },
     tpt_parents() {
      let ay = [{
        parent: 0,
        parentLabel: '根目录'
      }];
      if (this.tpt.data.parent != 0) {
        var nodedata = this.checkTreeSelectData();
        if (nodedata) {
          ay.push({
            parent: nodedata.albumId,
            parentLabel: nodedata.name
          })
        }
      }
      return ay;
    },
    storeUsedPercent(){
      let dft = 1;
      let info = this.storeInfo;
      if(info && info.used ){
        dft = info.used * 100 / info.total;
      }
      let ret = dft.toFixed(1) - 0;
      console.debug('使用率: ',ret)
      return ret
    }
  },
  watch: {
    "tp.data.type": function (val, old) {
      this.resource_server_url = serverConfig.getDefaultUploadUrl();
    }
  },
  methods: {
    catchPage() {
      if(this.tp.data.pageLink) {
        let url = this.getResUrl('/image/catch?page=' + this.tp.data.pageLink);
        $.ajax({
            type: 'GET',
            url: url,
            contentType: 'application/json;charset=utf-8',
            complete: () => {
              //nothing...
            },
            success: reps => {
              if(reps.state == 'SUCCESS') {
                this.tp.data.coverUrl = this.tp.data.url = this.tp.data.viewUrl = reps.url;
                this.tp.data.type = 'image';
              } else {
                this.$message.error(reps.msg)
              }
            },
            error: (xhr, status, _error) => {
              this.$message.error('系统错误.')
            }
        })
      } else {
        this.$message.error('请输入网页链接地址.')
      }
    },
    backToBefore(){
      let bp = breadPath(this.currentCategory, this.tpt_data, item => item.children, item => item.parent, item => item.albumId, item => item.data);
      let item = null;
      if(bp.length > 1){
        item =  bp[bp.length-2];
      }else{
        item = {name:null,parent:0};
      }
      this.breadPathClick(item);
    },
    dir_click(item){
      this.breadPathClick(item);
    },
    loadStoreInfo(){
      let url = '/system-capacity/resource-store';
      ajax_promise(url,'get',{}).then(result=>{
        this.storeInfo = result.data;
      });
    },
    initM3() {
      xiuxiu.embedSWF("imagesContainer", 2, "100%", "100%");
      xiuxiu.setUploadURL(serverConfig.getUrl("/upload"));
      xiuxiu.setUploadType(2);
      xiuxiu.setUploadDataFieldName("file");
      xiuxiu.onUploadResponse = function (data) {
        data = JSON.parse(data)
        appInstince.tp.data.coverUrl = appInstince.tp.data.url = appInstince.tp.data.viewUrl = data.url;
        appInstince.tp.data.type = 'image';
        appInstince.tp.m3Visible = false;
      }
      xiuxiu.onClose = function (id) {
        appInstince.tp.m3Visible = false;
      }
    },
    openM3() {
      this.tp.m3Visible = true;
    },
    breadPathClick(item) {
      let cc = this.currentCategory;
      if (cc.albumId == item.albumId) return;
      this.currentCategory = item;
      this.loadTpTypeData(item);
    },
    getUploadUrl(type) {
      return serverConfig.getDefaultUploadUrl(type);
    },
    submitUpload() {
      this.$refs.upload.submit();
    },
    clearChose() {
      console.log('清空选中文件');
      this.$refs.upload.clearFiles();
      this.importResource.fileList = [];
    },
    handleSuccess(response, file, fileList) {
      console.log('文件上传成功', arguments);
      this.importResource.fileList.push(response);
      if (fileList.length == this.importResource.fileList.length) {
        console.log('所有文件上传完成');
        if (this.importResource.commitOnUpload) this.saveResources();
      }
    },
    handleError(err, file, fileList){
      let msg = JSON.parse(err.message).msg;
      $message(msg,'error',this);
    },
    handleRemove(file, fileList) {
      console.log('remove file ', arguments);
      this.importResource.fileList = this.importResource.fileList.filter(item => file.name != item.original);
    },
    handlePreview(file) {
      console.log(file);
    },
    saveResources() {
      let me = this;
      if (this.importResource.nameType == 'fileName') {
        this.importResource.data.name = "占位符"
      }
      if (this.importResource.fileList.length == 0) {
        $message('请选择上传的文件!', 'warning', this);
        return;
      }
      this.$refs.importResForm.validate(function (valid) {
        if (!valid) {
          return false;
        }

        let array = [];
        me.importResource.fileList.forEach(item => {
          let obj = {
            description: me.importResource.data.description,
            url: item.url,
            contentType: item.type,
            type: item.type.split('/')[0],
            size: item.size,
            timeLength: item.length
          };

          if (obj.type == 'image') {
            obj.coverUrl = item.thumbnail ? item.thumbnail : item.url;
          } else {
            obj.coverUrl = item.screenshot;
          }

          if (me.importResource.nameType == 'fileName') {
            obj.name = item.original;
          } else {
            obj.name = me.importResource.data.name;
          }
          let ids = me.importResource.data.albumIds
          obj.albumId = ids[ids.length - 1];
          array.push(obj);
        });
        console.log(array);
        ajax_json("/resource/Materials", "post", array, function (result) {
          if (result.status) {
            me.importResource.visiable = false
            me.loadTreeData();
            me.reloadTpTypeData();
            me.resetImport();
          }
        });

      });
    },
    importResources() {
      this.importResource.visiable = true;
    },
    resetImport() {
      this.clearChose();
      this.importResource.data.name = '';
      this.importResource.data.description = '';
      this.importResource.data.albumIds = [];
    },
    treeContextmenu(event, data, node, ins) {
      this.$refs.treeContextMenu.showMenu(event,data.data);
      this.curContextData = data.data;
    },
    contextMenuClick(menuItem,datas) {
      this.curContextData = datas[0];
      console.log('菜单点击事件', menuItem);
      if (menuItem.label == '增加分类') {
        this.addTemplateType();
      }
      if (menuItem.label == '修改分类') {
        this.updateTemplateType();
      }
      if (menuItem.label == '删除分类') {
        this.deleteTemplateType();
      }
      this.curContextData = null;
    },

    card_hover(it) {
      it.showtoolbar = true;
    },
    card_leave(it) {
      it.showtoolbar = false;
    },

    //查询模板
    searchTemplate() {
      this.currentCategory.name = '所有类别';
      this.loadTpTypeData();
      this.loadTreeData();
    },
    // 加载类别树的数据
    loadTreeData: function () {
      var ins = this;
      let data = {
        keyword: this.keyword
      };
      ajax("/MaterialAlbum/Album", "get", data, function (result) {
        ins.tpt_data = ins.init_tpt_data(result.data);
        ins.tpt_data_normal = ins.toNormalData(result.data);
      });
    },
    init_tpt_data(data) {
      data.forEach(item => {
        item.data.showtoolbar = false;
      })
      console.log(data);
      return data;
    },
    //转化为普通数据
    toNormalData(data) {
      function next(obj, chi) {
        if (chi) {
          let ay = [];
          chi.forEach(v => {
            let o = v.data;
            ay.push(o);
            next(o,v.children);
          });
          obj.children = ay;
        } else {
          return;
        }
      }

      let ret = [];
      data.map((v) => {
        let obj = v.data;
        ret.push(obj);
        next(obj, v.children);
      });
      return ret;
    },

    // 加载分类数据
    loadTpTypeData: function (data) {
      let ins = this;
      if (data) {
        this.currentCategory = data;
      } else {
        data = {};
      }
      let url = '/resource/Material/' + this.tpager.current + '-' + this.tpager.size;
      data.keyword = this.keyword;
      ajax_json(url, "post", data, function (result) {
        ins.tps = ins.initData(result.data);
        ins.tpager.total = result.pager.total;
      });
    }, // 重新加载分类数据
    reloadTpTypeData: function () {
      var node = this.$refs.tree.getCurrentNode();
      this.loadTpTypeData(node ? node.data : null, this);
    },
    //获取选中树节点数据
    checkTreeSelectData: function () {
      var node = this.$refs.tree.getCurrentNode();
      if (!node && !this.curContextData) {
        $message("请先选择要操作的位置", "warning", this)
        return null;
      }
      let data =  this.curContextData ? this.curContextData : node.data;
      return data.albumId != this.beforeDelete.albumId?data:null;
    },
    initData(data) {
      data.forEach(element => {
        element.showtoolbar = false;
        element.cfv = false;
      });
      return data;
    },
    // 增加模板类别
    addTemplateType: function () {
      var nodedata = this.checkTreeSelectData();
      if (!nodedata) {
        nodedata = {
          albumId: 0,
          name: '根目录'
        }
      }

      var tpt = this.tpt;
      tpt.update = false;
      tpt.title = "新增分类类别";
      tpt.visible = true;
      tpt.data = {
        parent: nodedata.albumId,
        parentLabel: nodedata.name,
        orderNum: 0
      };
    },
    // 修改模板类别
    updateTemplateType: function () {
      var nodedata = this.checkTreeSelectData();
      if (!nodedata) return;

      var tpt = this.tpt;
      tpt.title = "修改分类类别";
      tpt.update = true;
      tpt.visible = true;
      tpt.data = nodedata;
    },
    // 保存或者更新数据
    saveOrUpdateTemplateType: function () {
      var ins = this;
      var tpt = this.tpt;
      if (tpt.data.parentLabel == '根目录') {
        tpt.data.parent = 0;
      }
      this.$refs.tptForm.validate(function (valid) {
        if (!valid) {
          return false;
        }
        if (tpt.update) {
          // 更新数据
          ajax_json("/MaterialAlbum/Album", "put", tpt.data, function (result) {
            if (result.status) {
              tpt.visible = false;
              ins.loadTreeData();
              //拼接图片恢复显示
              tpt.m3Visible = true;
            }
          });
        } else {
          // 新增数据
          ajax_json("/MaterialAlbum/Album", "post", tpt.data, function (result) {
            if (result.status) {
              tpt.visible = false;
              ins.loadTreeData();
              //拼接图片恢复显示
              tpt.m3Visible = true;
            }
          });
        }
      });
    },
    deleteTemplateType: function () {
      var ins = this;
      var tpt = this.tpt;
      var nodedata = this.checkTreeSelectData();
      if (!nodedata) return;
      if (nodedata.children && nodedata.children.length > 0) {
        $message("本节点包含子节点,如需删除请先删除子节点。", "warning", this);
        return;
      }
      this.$confirm(`此操作将『${nodedata.name}』, 是否继续?`, "提示", {
        type: "warning"
      }).then(function () {
        // 删除数据
        ajax_json("/MaterialAlbum/Album/" + nodedata.albumId, "delete", null, function (
          result
        ) {
          if (result.status) {
            tpt.visible = false;
            ins.loadTreeData();
            ins.beforeDelete = nodedata;
          }
        });
      });
    },
    // 类别点击
    tptTreeClick: function (_data, node) {
      var ins = this;
      this.tpager.current = 1;
      var data = _data.data; // 类别节点数据
      this.loadTpTypeData(data, ins);
    },
    //预览模板
    viewTemplate: function (tp) {
      this.resourceView.type = tp.type;
      if (tp.type == 'text') {
        _editor.setContent(tp.content);
        _editor.execCommand("preview");

        _editor.setContent('');
      } else {
        this.resourceView.url = tp.url;
        this.resourceView.title = tp.name;
        this.resourceView.visible = true;
      }
    },
    // 新增模板
    addTemplate: function () {
      this.resetTemplate();

      this.tp.visible = true;
      this.tp.update = false;
      this.tp.title = "素材上传";

    },
    //重置模板类别
    resetTemplate() {
      this.tp.data = {
        title: "",
        content: "",
        albumId: '',
        albumIds: [],
        url: '',
        type: 'image',
        description: '',
        viewUrl: '',
        typeDisable: false,
        coverUrl: ''
      };
      _editor.setContent("");
    },
    // 修改模板
    updateTemplate: function (tp) {
      this.resetTemplate();
      this.tp.visible = true;
      this.tp.update = true;
      this.tp.title = "修改素材";
      this.tp.data = tp;
      tp.typeDisable = true;
      if (tp.content) _editor.setContent(tp.content);
      var ids = getTpTypeIds(this.tp.data, this.tpt_data);
      this.tp.data.albumIds = ids;
    },
    // 删除模板
    delTemplate: function (tp) {
      var ins = this;
      var tpId = tp.materialId;
      if (!tpId) {
        $message("请选择要删除的素材!", "warning", ins);
        return;
      }
      var url = "/resource/Material/" + tpId;
      ajax_json(url, "delete", null, function (result) {
        if (result.status) {
          ins.loadTreeData();
          ins.reloadTpTypeData();
        }
      });
    },
    saveOrUpdateTemplate: function () {
      var ins = this;
      var tp = this.tp;
      this.$refs.tpForm.validate(function (valid) {
        if (!valid) return false;
        // 更新tpTypeIds
        var ids = tp.data.albumIds;
        tp.data.albumId = ids[ids.length - 1];
        tp.data.content = _editor.getContent();
        if (tp.data.type == 'text' && !_editor.hasContents()) {
          $message("请输入内容!", "warning", ins);
          return;
        }
        console.log(tp.data);
        if (tp.update) {
          // 更新数据
          ajax_json("/resource/Material", "put", tp.data, function (result) {
            if (result.status) {
              tp.visible = false;
              ins.loadTreeData();
              ins.reloadTpTypeData();
              ins.resetTemplate();
            }
          });
        } else {
          // 新增数据
          ajax_json("/resource/Material", "post", tp.data, function (result) {
            if (result.status) {
              tp.visible = false;
              ins.loadTreeData();
              ins.reloadTpTypeData();
              ins.resetTemplate();
            }
          });
        }
      });
    },
    handleAvatarSuccess(res, file) {
      console.log('上传资源信息:', res.msg);
      let res_type = this.tp.data.type;
      this.tp.data.contentType = res.type;
      this.tp.data.size = res.size;
      if (res_type == 'image') {
        this.tp.data.coverUrl = res.thumbnail ? res.thumbnail : res.url;
      } else {
        this.tp.data.coverUrl = res.screenshot;
      }
      this.tp.data.url = res.url;
      this.tp.data.original = res.original;
      this.tp.data.timeLength = res.length; //时间长度
      if (res_type == 'text') {
        this.tp.data.contentType = "text/html";
        this.tp.data.coverUrl = res.thumbnail ? res.thumbnail : res.url;
        this.tp.data.url = null;
      }
      if (res_type == 'audio') {
        if (!this.tp.data.coverUrl) this.tp.data.coverUrl = '/assets/img/timg.png';
      }
    },
    beforeAvatarUpload(file) {
      const isType = file.type.startsWith('image');
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isType) {
        this.$message.error('上传封面不许为图片!');
      }
      if (!isLt2M) {
        this.$message.error('上传资源大小不能超过 2MB!');
      }
      return isType && isLt2M;
    },
    beforeResourceUpload(file) {
      let type = this.tp.data.type;
      const isType = file.type.startsWith(type);
      const isLt2M = file.size / 1024 / 1024 < 200;

      if (!isType) {
        this.$message.error('上传文件资源和选择资源类型不符!');
      }
      if (!isLt2M) {
        this.$message.error('上传资源大小不能超过 200MB!');
      }
      return isType && isLt2M;
    },
    choseProgramTemplate(pt) {
      console.log("已选择节目模版", pt)
      this.ptw.visiable = false;
      this.tp.data.programTemplate = pt.programId;
      this.tp.data.programTemplateName = pt.name;
      this.tp.programTempate = pt;
      this.tp.data.categoryId = pt.categoryId;
    },
    handleSizeChange(val) {
      this.tpager.size = val;
      this.loadTpTypeData(this.currentCategory)
    },
    handleCurrentChange(val) {
      this.tpager.current = val;
      this.loadTpTypeData(this.currentCategory)
    },
    getResUrl(url) {
      return serverConfig.getUrl(url);
    },
    resourceViewClose() {
      //暂停视频/音频
      $('.videoView').trigger('pause');
      $('.audioView').trigger('pause');
    },
    allowDrag(draggingNode) {
      //内置数据不能拖拽
      return !draggingNode.data.data.builtin; 
    },
    treeDrapDrop(from, to, dropType, ev){
      console.log(arguments);
      let fd = from.data.data;
      let td = to.data.data;
      let ins = this;
      let data = {albumId:fd.albumId};
      //before、after、inner
      if(dropType == 'before'){
        data.parent = td.parent;
        data.orderNum = td.orderNum-1;
      }else if(dropType == 'after'){
        data.parent = td.parent;
        data.orderNum = td.orderNum+1;
      }else if(dropType == 'inner'){
        data.parent = td.albumId;
      }
      console.log('拖动结果: ',data)
      ajax_json("/MaterialAlbum/Album", "put", data, function (result) {
        if (result.status) {
          ins.loadTreeData();
          $message('拖动成功','success',ins);
        }
      });
    }
  }
});


// 编辑器
var _editor = UE.getEditor("templateText", {

});

function getTpTypeIds(data, tpt_data) {
  let ay = breadPath(data, tpt_data, item => item.children, item => item.parent, item => item.albumId, item => item.data);
  let r = ay.reverse().map(function (a) {
    return a.albumId;
  });
  //增加自身的信息
  r.push(data.albumId);
  return r;
}

window.appInstince = ins;