// pages/home/needs.js
import {
    uploadneed
} from '../../../api/api.js';
import util from "../../../utils/util";
const app = getApp()
Page({

    /**
     * 页面的初始数据
     */
    data: {
        ntitle: '',
        ncontent: '',
        nname: '',
        nphone: '',
        img: [],
        fileList: [],

        isShow: false
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {

        // if(!util.chekWxLogin)
        // {
        //   this.setData({
        //     isShow:true
        //   })
        // }
        if (app.isWxLogin(3)) {

        }
    },


    uploaddata: function () {
        // if (this.data.ntitle.length == 0) {
        //     wx.showToast({
        //         title: '请输入需求标题',
        //         icon: 'none'
        //     })
        //     return
        // } else
        if (!util.checkfilterstr(this.data.ntitle)) {
            wx.showToast({
                title: '标题不能含有特殊字符',
                icon: 'none'
            })
            return
        }
        else if (this.data.ncontent.length == 0) {
            wx.showToast({
                title: '请输入投诉/建议内容',
                icon: 'none'
            })
            return
        } else if (!util.checkfilterstr(this.data.ncontent)) {
            wx.showToast({
                title: '内容不能含有特殊字符',
                icon: 'none'
            })
            return
        }
        else if (this.data.nname.length == 0) {
            wx.showToast({
                title: '请输入您的姓名',
                icon: 'none'
            })
            return
        } else if (this.data.nphone.length == 0) {
            wx.showToast({
                title: '请输入您的联系电话',
                icon: 'none'
            })
            return
        } else if (!util.checkphone(this.data.nphone)) {
            wx.showToast({
                title: '请输入正确的联系电话',
                icon: 'none'
            })
            return
        }


        wx.showLoading({
            title: '加载中...',
        })
        var that = this
        var dic = {}
        dic.title = that.data.ntitle
        dic.content = decodeURI(that.data.ncontent)
        dic.contact = that.data.nname
        dic.phone = that.data.nphone
        dic.img = that.data.img ? that.data.img.toString() : ''
        dic.video = that.data.fileList ? that.data.fileList.toString() : ''
        uploadneed(dic).then(function (res) {
            wx.hideLoading({
                success: (res) => { },
            })
            console.log(res)
            if (res.code == 0) {
                wx.showToast({
                    title: '提交成功,请耐心等待审核',
                    icon: 'none'
                })
                setTimeout(() => {
                    // wx.navigateTo({
                    //     url: './myneed',
                    // })
                    wx.navigateBack({
                        delta: 1
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


    getcontent: function (e) {
        var value = e.detail.value
        this.setData({
            ncontent: value
        })
    },
    gettitle: function (e) {
        var value = e.detail.value
        this.setData({
            ntitle: value
        })
    },
    getname: function (e) {
        var value = e.detail.value
        this.setData({
            nname: value
        })
    },
    getphone: function (e) {
        var value = e.detail.value
        this.setData({
            nphone: value
        })
    },

    gologin: function () {
        wx.redirectTo({
            url: '/pages/login/login',
        })
    },
    goback: function () {
        wx.navigateBack({
            delta: 0,
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

    afterRead(event) {
        let that = this;
        const { file } = event.detail;
        wx.showLoading({
            title: '视频上传中...',
        })
        var token = wx.getStorageSync('token')
        // 当设置 mutiple 为 true 时, file 为数组格式，否则为对象格式
        wx.uploadFile({
            url: app.globalData.url + '/api/fastfile/upload/szrj-1256675456',
            filePath: file.url,
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
                that.setData({
                    fileList: [data.data.kpath]
                })

                wx.hideLoading()

            }
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

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {

    },

})