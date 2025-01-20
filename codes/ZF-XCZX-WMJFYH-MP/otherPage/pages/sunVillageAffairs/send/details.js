// otherPage/pages/sunVillageAffairs/collectiveMoney/details.js
import {
    resourcePublicById,
    presourcerecord
} from "../../../../api/sunVillage"
import WxParse from '../../../../wxParse/wxParse';
const app = getApp()
Page({

    /**
     * 页面的初始数据
     */
    data: {
        id: 0,
        detail: {},
        show: false,
        name: '',
        phone: '',
        beforeClose: null,

        top: 0,
        swiperCurrent: 1,
        imgArr: [],
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        let vm = this
        vm.setData({
            id: options.id,
            top: app.globalData.Custom.top,
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
        resourcePublicById(that.data.id).then(res => {
            wx.hideLoading({
                success: (res) => {},
            })
            if (res.code == 0) {
                if (res.data.content) {
                    WxParse.wxParse('rule', 'html', res.data.content, this, 0);
                }
                res.data.allImg = [res.data.assetsContent.content]

                if (res.data.assetsContent.file) {
                    let arr = JSON.parse(res.data.assetsContent.file)
                    arr.forEach(a => {
                        let index = a.label.lastIndexOf('.')
                        let index2 = a.label.length
                        var type = a.label.substr(index + 1, index2)
                        a.type = type
                    })
                    res.data.assetsContent.newFile = arr
                }
                if (res.data.assetsContentAutoList) {
                    res.data.assetsContentAutoList.forEach(l => {
                        if (l.name == '地址') {
                            res.data.address = l.val
                        }
                        if (l.name == '状态') {
                            res.data.status = l.val
                        }
                    })
                }
                that.setData({
                    detail: res.data
                })
            }
        })
    },
    goPhone() {
        wx.makePhoneCall({
            phoneNumber: this.data.detail.phone,
        })
    },
    messageClick() {
        this.setData({
            show: true,
            name: '',
            phone: ''
        })
    },
    onClose() {
        this.setData({
            show: false
        })
    },
    formSubmit() {
        let vm = this
        if (vm.data.name == '') {
            wx.showToast({
                title: '请输入需要登记的内容',
                icon: 'none'
            })
            return vm.dialogClost()
        }
        if (vm.data.phone == '') {
            wx.showToast({
                title: '请输入联系方式',
                icon: 'none'
            })
            return vm.dialogClost()
        }
        presourcerecord({
            aid: vm.data.detail.aid,
            content: vm.data.name,
            type: vm.data.detail.type,
            phone: vm.data.phone,
            did: vm.data.detail.did,
            dsid: vm.data.detail.dsid,
        }).then(res => {
            if (res.code == 0) {
                wx.showToast({
                    title: '提交成功',
                    icon: 'none'
                })
                vm.setData({
                    show: false
                })
            }
            console.log();
        })

        console.log(vm.data.name, 'vm.data.name');
    },
    dialogClost() {
        this.setData({
            beforeClose: (action) => {
                new Promise((resolve) => {
                    if (action == 'confirm') {
                        resolve(false)
                    } else {
                        resolve(true)
                    }
                })
            }
        })
    },
    bindName(e) {
        this.setData({
            name: e.detail.value
        })
    },
    bindPhone(e) {
        this.setData({
            phone: e.detail.value
        })
    },
    previewImage(e) {
        console.log(e,'--------------e');
        wx.previewImage({
            urls: [e.currentTarget.dataset.url], // 需要预览的图片链接列表
            complete:function(c){
                wx.hideLoading({
                    success: (res) => {},
                })
            }
        })
    },
    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {

        return {
            title: this.data.detail.assetsContent.name,
            imageUrl: this.data.detail.assetsContent.content,
            path: '/otherPage/pages/sunVillageAffairs/send/details?id=' + this.data.id,
        }
    },
    onShareTimeline: function () {
        return {
            title: this.data.detail.assetsContent.name,
            imageUrl: this.data.detail.assetsContent.content,
            query: 'id=' + this.data.id,
        }
    },
})