var ContextMenu = {
    info: {
        name: 'context-menu', //注册组件名
        template_url: '/components/common/context-menu.view.html',
        author: 'Wangch',
        descript: '右键菜单'
    },
    props: {
        //菜单数据
        data: {
            type: Array,
            default: () => {
                return [{
                    label: '增加',
                    icon: 'el-icon-plus',
                    type: 'success'
                }, {
                    label: '修改',
                    icon: 'el-icon-edit',
                    type: 'primary'
                }, {
                    label: '删除',
                    icon: 'el-icon-delete',
                    type: 'danger'
                }];
            }
        },
        //点击后关闭
        hideOnClick: {
            type: Boolean,
            default: true
        },
        //关闭超时时间 毫秒
        closeTimeOut: {
            type: Number,
            default: 1000
        },
        //是否显示菜单
        visiable: {
            type: Boolean,
            default: true
        },
        mouseEvent: {
            type: MouseEvent,
            default: () => {
                return {};
            }
        }

    },
    data() {
        return {
            closeTimer: null,
            position: {
                x: 333,
                y: 444
            }
        };
    },
    watch: {
        mouseEvent(val, oldVal) {
            this.position.x = val.x - 10;
            this.position.y = val.y - 10;
            console.log('change', this.position);
        }
    },
    computed: {
    },
    methods: {
        getTypeClass(type) {
            if (type == 'primary') return 'el-button--primary';
            if (type == 'danger') return 'el-button--danger';
            if (type == 'success') return 'el-button--success';
            if (type == 'info') return 'el-button--info';
            if (type == 'warning') return 'el-button--warning';

            return ' el-select-dropdown__item';
        },
        //定时器 延时5秒关闭菜单
        mouseleave() {
            console.debug('mouse leave, 创建倒计时关闭定时器', arguments);
            let me = this;
            //开始计时
            this.closeTimer = setTimeout(() => {
                me.hideMenu();
            }, this.closeTimeOut);
        },
        mouseenter() {
            //清除定时器
            console.debug('mouse enter,清除关闭定时器', arguments);
            this.clearTimer();
        },
        clearTimer() {
            if (this.closeTimer) {
                clearTimeout(this.closeTimer);
                this.closeTimer = null;
            }
        },
        menuItemClick(item) {
            console.debug('menu item click', arguments);
            if (this.hideOnClick) {
                this.hideMenu();
            }
            this.$emit('click', item);
        },
        //隐藏菜单
        hideMenu() {
            console.debug('隐藏菜单');
            this.clearTimer();
            this.$emit('close');
            this.$emit('update:visiable', false);
        }
    },
    template:require("./context-menu.view.html")
}

export default ContextMenu;