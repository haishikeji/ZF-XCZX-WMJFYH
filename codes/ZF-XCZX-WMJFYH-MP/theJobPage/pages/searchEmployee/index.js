// theJobPage/pages/searchEmployee/index.js
import { infoList, villagesList } from '../../../api/newApi.js';
const app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
        choosePopshow: false,
        columns: [],
        didColumns: [],
        xzColumns: [
            {
                name: '不限',
                value: ''
            },
            {
                name: '3000以下',
                value: '3000以下'
            },

            {
                name: '3000-5000',
                value: '3000-5000'
            },
            {
                name: '5000-8000',
                value: '5000-8000'
            },
        ],
        did: '',
        didName: '',
        xz: '',
        xzName: '',
        list: [],
        page: 1,
        hasmore: true,
        value: '',
        popTitle: '请选择位置',
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        villagesList({
            key: 1,
        }).then(res => {
            this.setData({
                didColumns: [
                    {
                        name: '不限',
                        deptId: ''
                    },
                    ...res.data
                ]
            })
        })
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
        this.setData({
            fontMultiple: app.setFontMultiple()
        })
        this.refresh()
    },
    search(e) {
        this.setData({
            value: e.detail.value
        })
    },
    refresh() {
        this.setData({
            list: [],
            page: 1,
            hasmore: true,
        })
        this.getlist()
    },
    getlist() {
        wx.showLoading({
            title: '加载中...',
        })
        var that = this
        var dic = {}
        dic.pageNo = that.data.page
        dic.title = that.data.value
        dic.did = that.data.did
        dic.pageSize = 20
        dic.type = 1
        dic.cate = 2
        if (that.data.xz && that.data.xz !== '3000以下') {
            dic.minmoney = that.data.xz.split('-')[0]
            dic.maxmoney = that.data.xz.split('-')[1]
        } else if (that.data.xz) {
            dic.minmoney = 0
            dic.maxmoney = 3000
        }
        infoList(dic).then(function (res) {
            wx.hideLoading({
                success: (res) => { },
            })
            wx.stopPullDownRefresh({
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
                for (var i = 0; i < arr.length; i++) {
                    var obj = arr[i]
                }
                arr.forEach(item => {
                    item.timer = that.calculateRelativeTime(item.createTime)
                });
                that.setData({
                    list: that.data.list.concat(arr)
                })
            } else {
                wx.showToast({
                    title: res.msg,
                    icon: 'none'
                })
            }

        })
    },
    goDetail(e) {
        wx.navigateTo({
            url: './detail?id=' + e.currentTarget.dataset.id,
        })
    },
    chooseShow(e) {
        const type = e.currentTarget.dataset.type
        this.setData({
            choosePopshow: true,
            type: type,
            columns: type == 1 ? this.data.didColumns : this.data.xzColumns,
            popTitle: type == 1 ? '请选择位置' : '请选择薪资',
        })
    },
    onCancel() {
        this.setData({
            choosePopshow: false
        })
    },
    onConfirm(event) {
        let obj = {}
        if (this.data.type == 1) {
            obj.did = event.detail.value.deptId
            obj.didName = event.detail.value.name
        } else {
            obj.xz = event.detail.value.value
            obj.xzName = event.detail.value.name
        }
        this.setData(obj)
        this.onCancel();
    },
    goAdd() {
        const role = wx.getStorageSync('role')
        if (role == 0) {
            wx.showModal({
                title: '提示',
                content: '您当前是游客，该操作需要普通用户登录，是否进行普通用户登录？',
                success(res) {
                    if (res.confirm) {
                        wx.reLaunch({
                            url: '/pages/login/login',
                        })
                    } else if (res.cancel) {
                        console.log('用户点击取消')
                    }
                }
            })
            // wx.showToast({
            //     title: '游客登录不能发布工作!',
            //     icon: 'none'
            // })
        } else {
            wx.navigateTo({
                url: './add',
            })
        }
    },
    calculateRelativeTime(publicationTime) {
        const currentTime = new Date();
        const publicationDate = new Date(publicationTime);

        // 将时间戳转换为当天的开始时间（午夜）
        const startOfToday = new Date(
            currentTime.getFullYear(),
            currentTime.getMonth(),
            currentTime.getDate()
        );

        // 计算时间差（毫秒）
        const timeDifference = currentTime - publicationDate;
        const oneDay = 24 * 60 * 60 * 1000;

        if (timeDifference < oneDay && publicationDate >= startOfToday) {
            return '今天';
        } else if (timeDifference < oneDay * 2) {
            return '昨天';
        } else if (timeDifference < oneDay * 3) {
            return '前天';
        } else {
            // 返回入参发布时间
            return publicationTime.substring(0, 10);

        }
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