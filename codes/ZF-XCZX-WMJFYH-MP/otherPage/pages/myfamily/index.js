// pages/mine/myfamily.js
import {
    bbmemberlist
} from '../../../api/api.js';
const app = getApp();

Page({

    /**
     * 页面的初始数据
     */
    data: {
        list: [],
        page: 1,
        hasmore: true,
        pid: '',//户主id

        curruid: ""
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        if (options.id) {
            this.setData({
                pid: options.id
            })
        }

        var userinfo = wx.getStorageSync('userinfo')
        this.setData({
            curruid: userinfo.id || ''
        })
        console.log(userinfo)
    },

    getlist: function () {
        wx.showLoading({
            title: '加载中...',
        })
        var that = this
        var userinfo = wx.getStorageSync('userinfo')

        bbmemberlist({ pageNo: that.data.page, pageSize: 20, uid: that.data.pid }).then(function (res) {
            wx.hideLoading({
                success: (res) => { },
            })
            wx.stopPullDownRefresh({
                success: (res) => { },
            })
            console.log(res)
            if (res.code == 0) {
                var arr = res.data.records
                arr.forEach(i => {
                    i.phone = i.phone ? app.maskPhoneNumber(i.phone) : '';
                    i.idcard = i.idcard ? app.maskIDNumber(i.idcard) : '';
                })

                if (arr.length < 20) {
                    that.setData({
                        hasmore: false
                    })
                }

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
    gopage: function (e) {
        var obj = e.currentTarget.dataset.item
        wx.setStorageSync('currmember', obj)
        wx.navigateTo({
            url: '../mine/editfamily',
        })
    },
    addclick: function () {
        wx.navigateTo({
            url: '../mine/editfamily?id=' + this.data.pid + '&addhu=-1',
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
        this.setData({
            fontMultiple: app.setFontMultiple(),

            list: [],
            page: 1,
            hasmore: true
        })
        this.getlist()
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
            list: [],
            page: 1,
            hasmore: true
        })
        this.getlist()
    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {
        if (this.data.hasmore) {
            this.data.page++
            this.getlist()
        }
    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {

    }
})