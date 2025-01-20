// otherPage/pages/cadre/index.js
const app = getApp();
import { taskcate } from '../../../api/api'
import { allintegral, alltask, get_task_audit_list, get_user_audit_list, statistics, audit_user, deduct_result, get_task_user_count, shlist, shhe, rolescore } from '../../../api/newApi'
Page({

    /**
     * 页面的初始数据
     */
    data: {
        tabIndex: 1,
        tabListIndex: 1,
        currentCun: wx.getStorageSync('currentCun'),
        option: {},


        list: [],
        page: 1,
        hasmore: true,

        popShow: false,

        columns: [],
        classIndex: 0,
        columns2: [{
            id: '',
            name: '全部'
        }, {
            id: 1,
            type: 1,
            name: '人才市场'
        }, {
            id: 2,
            type: 1,
            name: '招聘中心'
        }],
        classIndex2: 0,
        points: 0,
        cdids: [],


        lang: app.getLang(),
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {

        wx.hideHomeButton();
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

    },
    goRecord() {
        app.message().then(() => {
            wx.navigateTo({
                url: './record',
            })
        })
    },
    goZhenDataAnalysis() {
        app.message().then(() => {
            wx.navigateTo({
                url: './../zhenDataAnalysis/index',
            })
        })
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
        if (app.isWxLogin(3)) {
            let userInfo = wx.getStorageSync('userInfo');
            let cdids = [];
            userInfo.cdids ? cdids = userInfo.cdids.split(",") : '';
            this.setData({
                currentCun: wx.getStorageSync('currentCun'),
                list: [],
                page: 1,
                hasmore: true,
                userInfo: userInfo,
                cdids: cdids,
                lang: app.getLang(),
            })

            this.getData();

            rolescore().then(r => {
                if (r.code == 0) {
                    this.setData({
                        points: r.data
                    })
                }

            })
        }
        wx.hideHomeButton();
    },
    initList() {
        let vm = this;
        vm.setData({
            list: [],
            page: 1,
            hasmore: true,
        })
        vm.getlist();
        get_task_user_count({
            cid: vm.data.columns.length ? vm.data.columns[vm.data.classIndex].id : '',
            cate: vm.data.columns2[vm.data.classIndex2].id,
            type: vm.data.columns2[vm.data.classIndex2].type,
            audit: (wx.getStorageSync('currentCun')).deptId
        }).then(r => {
            if (r.code == 0) {
                vm.setData({
                    userNum: r.data.userCount,
                    taskNum: r.data.taskCount,
                    infoCount: r.data.infoCount
                })
            }

        })
    },
    fabuShenhe(e) {
        let vm = this;
        app.message().then(() => {
            console.log(e.mark)
            let type = e.mark.type;
            let item = e.mark.item;

            wx.showModal({
                title: '提示',
                content: '确定要' + (type == 1 ? '同意' : '拒绝') + ' 该申请吗？',
                complete: (res) => {
                    if (res.cancel) { }
                    if (res.confirm) {
                        shhe({
                            id: item.id,
                            state: type
                        }).then(r => {
                            if (r.code == 0) {
                                wx.showToast({
                                    icon: 'none',
                                    title: '提交成功',
                                })
                                vm.initList();
                            }

                        })
                    }
                }
            })
        })

    },
    userShenhe(e) {
        let vm = this;
        app.message().then(() => {
            console.log(e.mark)
            let type = e.mark.type;
            let item = e.mark.item;
            let name = ' 申请人：' + item.name;
            if (this.data.userInfo.ztype == 1) {
                name = ' 申请队伍：' +item.zname
            }
            wx.showModal({
                title: '提示',
                content: '确定要' + (type == 1 ? '同意' : '拒绝') +  name + ' 的申请吗？',
                complete: (res) => {
                    if (res.cancel) { }
                    if (res.confirm) {
                        audit_user({
                            uid: item.id,
                            type: type
                        }).then(r => {
                            if (r.code == 0) {
                                wx.showToast({
                                    icon: 'none',
                                    title: '提交成功',
                                })
                                vm.initList();
                            }

                        })
                    }
                }
            })
        })
    },
    taskShenhe(e) {
        let vm = this;
        console.log(e.mark)
        app.message().then(() => {
            let type = e.mark.type;
            let item = e.mark.item;
            let name = ' 申请人：' + item.uname;
            if (this.data.userInfo.ztype == 1) {
                name = ' 申请队伍：' +item.zname
            }
            wx.showModal({
                title: '确定要' + (type == 1 ? '同意' : '拒绝') + name + ' 的申请吗？',
                editable: true,
                placeholderText: '请输入备注',
                complete: (res) => {
                    console.log(res)
                    if (res.cancel) { }
                    if (res.confirm) {
                        if (!res.content) {
                            wx.showToast({
                                icon: 'none',
                                title: '您未输入备注',
                            })
                        } else {
                            deduct_result({
                                id: item.id,
                                type: type,
                                msg: res.content,
                            }).then(r => {
                                if (r.code == 0) {
                                    wx.showToast({
                                        icon: 'none',
                                        title: '提交成功',
                                    })
                                    vm.initList();
                                }

                            })
                        }

                    }
                }
            })
        })
    },
    openClassPop() {
        this.setData({
            popShow: true
        })
    },
    popHide() {
        this.setData({
            popShow: false
        })
    },
    onConfirm(e) {
        console.log(e)
        this.setData({
            classIndex: e.detail.index,
        })
        this.initList();
        this.popHide();
    },

    openClassPop2() {
        this.setData({
            popShow2: true
        })
    },
    popHide2() {
        this.setData({
            popShow2: false
        })
    },
    onConfirm2(e) {
        console.log(e)
        this.setData({
            classIndex2: e.detail.index,
        })
        this.initList();
        this.popHide2();
    },
    getData() {
        let vm = this;

        statistics({ cdid: vm.data.currentCun.deptId }).then(r1 => {
            if (r1.code == 0) {
                this.setData({
                    topData: r1.data
                })
            }
        })
        vm.getalltask();

        // vm.setData({
        //     list: [],
        //     page: 1,
        //     hasmore: true,
        // })
        // vm.getlist();
        vm.initList();

        taskcate({
            type: 1, ...app.getCunId()
        }).then(function (res) {
            console.log(res)
            if (res.code == 0) {
                var arr = []
                var dic = {
                    id: '',
                    name: '全部'
                }
                arr.push(dic)
                vm.setData({
                    columns: arr.concat(res.data)
                })
            } else {
                wx.showToast({
                    title: res.msg,
                    icon: 'none'
                })
            }
        })

    },

    getlist() {
        wx.showLoading({
            title: '加载中...',
            mask: true
        })
        var that = this
        var dic = {}
        dic.pageNo = that.data.page;
        dic.pageSize = 20;
        dic.audit = (wx.getStorageSync('currentCun')).deptId;
        let func;
        if (that.data.tabListIndex == 1) {
            // let userInfo = wx.getStorageSync('userInfo')
            // if (userInfo.ztype == 1) {
            //     dic.ztype = 1;
            // }
            func = get_user_audit_list(dic);
        } else if (that.data.tabListIndex == 2) {
            if (that.data.classIndex != 0) {
                dic.cid = that.data.columns[that.data.classIndex].id
            }
            func = get_task_audit_list(dic);
        } else if (that.data.tabListIndex == 3) {
            dic.cdid = (wx.getStorageSync('currentCun')).deptId;
            if (that.data.classIndex2 != 0) {
                dic.cate = that.data.columns2[that.data.classIndex2].id
                dic.type = that.data.columns2[that.data.classIndex2].type
            }
            func = shlist(dic)
        }
        func.then(function (res) {
            wx.hideLoading({
                success: (res) => { },
            })
            console.log(res)
            if (res.code == 0) {
                var arr = res.data.records
                if (arr.length < 20) {
                    that.setData({
                        hasmore: false
                    })
                }
                arr.forEach(i => {
                    if (i.phone) {
                        i.phone = app.maskPhoneNumber(i.phone);
                    }
                    i.image = i.image ? i.image.split(",") : [];
                    i.createTime = i.createTime.slice(0, 16)
                    // if (i.result) {
                    //     i.result = i.result.split(",");
                    // }
                })
                that.setData({
                    list: that.data.list.concat(arr),
                })
            } else {
                wx.showToast({
                    title: res.msg,
                    icon: 'none'
                })
            }

        })

    },
    tuichu() {
        wx.showModal({
            title: '提示',
            content: '确定要退出登录吗？',
            complete: (res) => {
                if (res.cancel) {

                }

                if (res.confirm) {
                    wx.removeStorageSync('wxUserInfo');
                    wx.removeStorageSync('role');
                    wx.removeStorageSync('token');
                    wx.removeStorageSync('userInfo');
                    wx.removeStorageSync('currentCun');
                    wx.reLaunch({
                        url: '/pages/login/login',
                    })
                }
            }
        })
    },
    yulan(e) {
        let arr = [];
        e.mark.arr.forEach(i => {
            arr.push({ url: i })
        })
        wx.previewMedia({
            sources: arr,
            current: e.mark.index
        })
    },

    getalltask() {
        let vm = this;
        vm.setData({
            echartsShow: false
        })
        alltask({ cdid: vm.data.currentCun.deptId }).then(r => {
            if (r.code == 0) {
                let x = [], y = [];
                r.data.forEach(i => {
                    x.push(i.date == 1 ? '本月' : (i.date == 2 ? '本季度' : ((i.date == 3 ? '本年度' : ''))));
                    y.push(i.count)
                })
                vm.setOption(x, y)
            }
        })
    },
    getallintegral() {
        let vm = this;
        vm.setData({
            echartsShow: false
        })
        allintegral({ cdid: vm.data.currentCun.deptId }).then(r => {
            if (r.code == 0) {
                let x = [], y = [];
                r.data.forEach(i => {
                    x.push(i.date == 1 ? '本月' : (i.date == 2 ? '本季度' : ((i.date == 3 ? '本年度' : ''))));
                    y.push(i.count)
                })
                vm.setOption(x, y)
            }
        })
    },

    setOption(x, y) {
        let vm = this;
        let option = {
            title: {
                text: '数量统计',
                left: '3%',
                top: '5%'
            },
            grid: {
                show: false,
                left: '18%',
                top: '30%',
                right: '10%',
                bottom: '15%',
            },
            xAxis: {
                type: 'category',
                data: x,
                axisLine: {
                    lineStyle: {
                        color: ' #f6f6f6', // 设置 x 轴刻度线颜色
                    },
                },
                axisLabel: {
                    color: '#666', // 设置 x 轴刻度线文字颜色
                },
                splitLine: {
                    show: false, // 不显示 x 轴网格线
                },
                axisTick: {
                    show: false, // 不显示 x 轴刻度线上的突出短线
                },
            },
            yAxis: {
                type: 'value',
                axisLine: {
                    show: true,
                    lineStyle: {
                        color: '#F6F6F6', // 设置 y 轴刻度线颜色
                    },
                },
                axisTick: {
                    show: false, // 不显示 y 轴刻度线上的突出短线
                },
                axisLabel: {
                    color: '#666', // 设置 y 轴刻度线文字颜色
                },
                splitLine: {
                    show: false, // 不显示 y 轴网格线
                },
            },
            series: [
                {
                    data: y,
                    type: 'bar',
                    color: '#0ab481',
                    barWidth: '15%', //柱条的宽度，不设时自适应
                    itemStyle: {
                        borderRadius: [4, 4, 4, 4],
                    },
                }
            ]
        }

        this.setData({
            option,
            echartsShow: true
        })

    },
    goDataAnalysis() {
        app.message().then(() => {
            wx.navigateTo({
                url: '/otherPage/pages/dataAnalysis/index',
            })
        })
    },

    goPoints() {
        app.message().then(() => {
            wx.navigateTo({
                url: '/pointsPage/pages/task/index?type2=koufen',
            })
        })
    },
    goDetail(e) {
        app.message().then(() => {
            if (e.mark.type == 1 || e.mark.type == 2) {
                wx.navigateTo({
                    url: './detail?type=' + e.mark.type,
                })
            }
            if (e.mark.type == 3) {
                wx.navigateTo({
                    url: './task',
                })
            }
        })
    },
    changeTab(e) {
        app.message().then(() => {
            this.setData({
                tabIndex: e.mark.index
            })
            if (e.mark.index == 1) {
                this.getalltask();

            } else {
                this.getallintegral();
            }
        })
    },

    changeTabList(e) {
        app.message().then(() => {
            this.setData({
                tabListIndex: e.mark.index,
            })
            this.initList();
        })
    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh() {
    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom() {
        if (this.data.hasmore) {
            this.data.page++
            this.getlist()
        }
    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    }
})