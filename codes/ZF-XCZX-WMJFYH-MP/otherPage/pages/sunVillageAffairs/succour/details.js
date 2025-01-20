// otherPage/pages/sunVillageAffairs/collectiveMoney/details.js
import {
    assetscontentById
} from "../../../../api/sunVillage"
import WxParse from '../../../../wxParse/wxParse';
Page({

    /**
     * 页面的初始数据
     */
    data: {
        id: 0,
        detail: {},
        pid: 0,
        userInfo:{}
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        let vm = this
        vm.setData({
            id: options.id,
            pid: options.pid,
            userInfo:wx.getStorageSync('userInfo')
        })
        vm.getDetail()
    },
    // 详情
    getDetail() {
        let that = this
        wx.showLoading({
            title: '加载中...',
            mask: true
        })
        assetscontentById(that.data.id).then(res => {
            wx.hideLoading({
                success: (res) => {},
            })
            if (res.code == 0) {
                // if (res.data.content) {
                //     WxParse.wxParse('rule', 'html', res.data.content, this, 0);
                // }

                if (res.data.file) {
                    let arr = JSON.parse(res.data.file)
                    arr.forEach(a => {
                        let index = a.label.lastIndexOf('.')
                        let index2 = a.label.length
                        var type = a.label.substr(index + 1, index2)
                        a.type = type
                    })
                    res.data.newFile = arr
                }
                that.setData({
                    detail: res.data
                })
            }
        })
    },
    // 在线预览
    onLineLook(e) {
        let url = e.currentTarget.dataset.url
        wx.showLoading({
            title: '在线预览启动中...',
            mask: true
        })

        let index = url.lastIndexOf('.')
        let index2 = url.length
        var type = url.substr(index + 1, index2)
        console.log(type, 'type');
        if (type == 'png' || type == 'jpg') {
            wx.previewImage({
                urls: [url], // 需要预览的图片链接列表
                complete: function (c) {
                    wx.hideLoading({
                        success: (res) => {},
                    })
                }
            })
        } else {
            wx.downloadFile({
                url: url,
                success: function (res) {
                    var filePath = res.tempFilePath
                    wx.openDocument({
                        filePath: filePath,
                        fileType: type,
                        showMenu: true,
                        success: function (res) {
                            wx.hideLoading({
                                success: (res) => {},
                            })
                            console.log('打开文档成功')
                        },
                        fail: function (err) {
                            wx.hideLoading({
                                success: (res) => {},
                            })
                            console.log('打开文档失败')
                        },
                    })
                }
            })
        }
    },
    editBind() {
        wx.navigateTo({
            url: './add?pid=' + this.data.pid + '&cid=' + this.data.detail.cid + '&id=' + this.data.detail.id,
        })
    },
    //去投诉
    goComplain() {
        wx.navigateTo({
            url: '../onlineComplaint/notice?propertyDesc=补助救助-' + this.data.detail.name,
        })
    },
    onlookImg() {
        wx.previewImage({
            urls: [this.data.detail.content], // 需要预览的图片链接列表
            complete: function (c) {
                wx.hideLoading({
                    success: (res) => {},
                })
            }
        })
    }
})