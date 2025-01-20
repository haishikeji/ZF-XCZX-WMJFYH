// pages/task/finish.js

import {
    taskfinish,
    taskresult,
    xunchafinish
} from './../../../api/api.js';
import util from "../../../utils/util";
const app = getApp()
Page({

    /**
     * 页面的初始数据
     */
    data: {
        date: '',
        index: -1,
        isShow: false,
        content: '',
        tid: 0,
        img: [],
        type: "", //1是整改2巡查登记
        selectdata: null,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        this.setData({
            tid: options.id,
            type: options.type || ''
        })
    },

    bindDateChange: function (e) {
        console.log('picker发送选择改变，携带值为', e.detail.value)

        this.setData({
            date: e.detail.value,
            index: 1
        })
    },

    getcontent: function (e) {
        this.setData({
            content: e.detail.value
        })
    },
    submit: function () {
        app.message().then(() => {
            if (!this.data.selectdata && this.data.type == 2) {
                wx.showToast({
                    title: '请选择居民',
                    icon: 'none'
                })
                return
            }


            if (this.data.content.length == 0 && this.data.type != 1) {
                wx.showToast({
                    title: '请填写完成情况',
                    icon: 'none'
                })
                return
            } else if (!util.checkfilterstr(this.data.content)) {
                wx.showToast({
                    title: '内容不能含有特殊字符',
                    icon: 'none'
                })
                return
            }else if (this.data.img.length == 0) {
                wx.showToast({
                    title: '请上传任务照片',
                    icon: 'none'
                })
                return
            }

            this.setData({
                isShow: true
            })
        })

    },
    finishtask: function () {
        wx.showLoading({
            title: '正在提交...',
            mask:true
        })

        var cfun
        if (this.data.type == 1) {
            var dic = {}
            dic.id = this.data.tid
            // dic.result = this.data.img.join(',')
            dic.image = this.data.img.join(',')
            cfun = taskresult(dic)
        } else if (this.data.type == 2) {
            var dic = {}
            dic.tid = this.data.tid
            // dic.result = this.data.img.join(',')
            dic.image = this.data.img.join(',')
            dic.mark = this.data.content
            dic.uid = this.data.selectdata.id
            cfun = xunchafinish(dic)
        }
        else {
            var dic = {}
            dic.tid = this.data.tid
            // dic.result = this.data.img.join(',')
            dic.image = this.data.img.join(',')
            dic.mark = this.data.content
            cfun = taskfinish(dic)
        }
        cfun.then(function (res) {
            wx.hideLoading({
                success: (res) => { },
            })
            console.log(res)
            if (res.code == 0) {
                wx.showToast({
                    title: '提交成功,请等待后台审核',
                    icon: 'none'
                })
                setTimeout(() => {
                    wx.navigateBack({
                        delta: 0,
                    })
                }, 2000);
            } else {
                wx.showToast({
                    title: res.msg,
                    icon: 'none'
                })
            }
        })
    },

    onChangeTap: function (e) {
        wx.showLoading({
            title: '图片上传中...',
        })
        var count = 0
        var arr = []
        console.log(e)
        var token = wx.getStorageSync('token')
        var that = this
        for (var i = 0; i < e.detail.all.length; i++) {
            wx.uploadFile({
                url: app.globalData.url + '/api/fastfile/upload/szrj-1256675456',
                filePath: e.detail.all[i],
                name: 'file',
                formData: {
                    'file': 'file'
                },

                header: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    "Authorization": token
                },
                success(res) {
                    console.log(res)

                    var data = JSON.parse(res.data);
                    arr.push(data.data.kpath)
                    that.setData({
                        img: arr
                    })

                    count++
                    if (count >= e.detail.all.length) {
                        wx.hideLoading()
                    }

                }
            })
        }

    },
    onRemoveTap: function (e) {
        var arr = this.data.img
        arr.splice(e.detail.index, 1)
        this.setData({
            img: arr
        })
    },

    gosearch: function () {
        wx.navigateTo({
            url: '../mine/xuncha/search',
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
        var item = wx.getStorageSync('selectuser')
        if (item) {
            this.setData({
                selectdata: item
            })

            setTimeout(() => {
                wx.removeStorageSync('selectuser')
            }, 100);
        }
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

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {

    }
})