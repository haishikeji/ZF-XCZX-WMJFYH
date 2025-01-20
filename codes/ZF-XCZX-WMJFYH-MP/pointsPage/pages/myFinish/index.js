// pointsPage/pages/myFinish/index.js
import {
    taskrecode
} from './../../../api/newApi';
const app = getApp()
Page({

    /**
     * 页面的初始数据
     */
    data: {

        CustomBar: app.globalData.CustomBar,
        minDate: new Date().getTime() - 365 * 3 * 24 * 60 * 60 * 1000,
        maxDate: new Date().getTime(),
        list: [],
        page: 1,
        hasmore: true,

        showFlag: false

    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        console.log(options)


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

        if (app.isWxLogin(3)) {
            this.setData({
                showFlag: true,
                role: wx.getStorageSync('role'),
                lang: app.getLang()
            })
            if (this.data.list.length == 0) {
                this.init();
            }
        }

    },
    riliPopShow() {
        this.setData({
            riliShow: true
        })
    },
    onClose() {
        this.setData({
            riliShow: false
        })
    },
    formatDate(date) {
        date = new Date(date);
        let y = date.getFullYear();
        let m = date.getMonth() + 1;
        m < 10 ? (m = '0' + m) : '';
        let d = date.getDate();
        d < 10 ? (d = '0' + d) : '';

        return y + '-' + m + '-' + d;
    },
    onConfirm(event) {
        const [start, end] = event.detail;
        this.setData({
            riliShow: false,
            start: this.formatDate(start),
            end: this.formatDate(end),
        });
        this.init();
    },

    init() {
        this.setData({
            page: 1,
            hasmore: true,
            list: []
        })
        this.getlist()
    },

    getlist: function () {
        wx.showLoading({
            title: '加载中...',
        })
        var that = this
        var dic = { ...app.getCunId() };
        dic.pageNo = that.data.page
        dic.pageSize = 20
        if (that.data.start && that.data.end) {
            dic.start = that.data.start + ' 00:00:00';
            dic.end = that.data.end + ' 23:59:59';
        }
        if (this.data.role == 4) {
            dic.ztype = 1;
        }

        taskrecode(dic).then(function (res) {
            wx.hideLoading({
                success: (res) => { },
            })
            // console.log(res)
            if (res.code == 0) {
                var arr = res.data.records
                arr.forEach(i => {
                    i.image = i.image ? i.image.split(",") : [];
                    i.shTime = i.createTime ? i.createTime.substring(0, 16) : '';
                })
                if (arr.length < 20) {
                    that.setData({
                        hasmore: false
                    })
                }
                that.setData({
                    list: that.data.list.concat(arr),
                    subindex: 0
                })
            } else {
                wx.showToast({
                    title: res.msg,
                    icon: 'none'
                })
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