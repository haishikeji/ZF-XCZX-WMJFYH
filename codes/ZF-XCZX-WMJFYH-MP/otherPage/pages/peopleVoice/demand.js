// pages/home/demand.js
import {
    getSuggest
} from '../../../api/api'
Page({

    /**
     * 页面的初始数据
     */
    data: {
        keyword: '',
        page: 1,
        list: [],
        hasmore: false,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        this.getList()
    },
    getList() {
        var that = this
        var dic = {}
        dic.pageNo = that.data.page
        dic.pageSize = 20
        dic.kw = that.data.keyword
        wx.showLoading({
            title: '加载中...',
        })
        getSuggest(dic).then(res => {
            wx.hideLoading({
                success: (res) => { },
            })
            wx.stopPullDownRefresh({
                success: (res) => { },
            })
            var arr = res.data.records
            console.log(arr)
            for (var i = 0; i < arr.length; i++) {
                var obj = arr[i]
                if (obj.img) {
                    obj.img = obj.img.split(',')
                }
                if (obj.createTime) {
                    obj.ctime = obj.createTime.substring(0, 16)
                }
            }
            if (arr.length < 20) {
                that.setData({
                    hasmore: false
                })
            }
            that.setData({
                list: that.data.list.concat(arr)
            })
        })
    },
    demandDetail(e) {
        var index = e.currentTarget.dataset.index
        var obj = this.data.list[index]
        wx.setStorageSync('currneed', obj)
        wx.navigateTo({
            url: './needdetail',
        })
    },
    getDemand() {
        wx.navigateTo({
            url: './needs',
        })
    },
    myDemand() {
        wx.navigateTo({
            url: './myneed',
        })
    },
    searchclick: function () {
        this.setData({
            page: 1,
            list: [],
            hasmore: true
        })
        this.getList()
    },
    getkeyword: function (e) {
        this.setData({
            keyword: e.detail.value
        })
    },
    search: function (e) {
        this.setData({
            keyword: e.detail.value,
            page: 1,
            list: [],
            hasmore: true
        })
        this.getList()
    },

    callPhone(e) {
        wx.makePhoneCall({
            phoneNumber: '15555555555',
            success(res) {
                console.log('拨打电话成功', res);
            },
            fail(err) {
                console.error('拨打电话失败', err);
            }
        });
    },
    goPage() {
        wx.navigateTo({
            url: '/otherPage/pages/peopleVoice/needs',
        })
    },
    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {

    },

    /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
    onPullDownRefresh: function () {
        this.setData({
            page: 1,
            list: [],
            hasmore: true
        })
        this.getList()
    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {
        if (this.data.hasmore) {
            this.data.page++
            this.getList()
        }
    },

})