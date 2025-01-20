// pages/home/integral/index.js
import { getshop, getShopList } from '../../../api/api.js';
import { Haversine, shopdata } from '../../../api/newApi.js';
const app = getApp();
Page({

    /**
     * 页面的初始数据
     */
    data: {
        shopInfo: null,
        shopData: [],
        myScore: '',
        shopList: [],
        page: 1,
        activeFlag: null,
        hasmore: true,
        zuijinId: null,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        // this.getshopData()
        if (options.scene) {
            this.setData({
                shopInfo: {
                    id: options.scene
                }
            })
        } else if (options.id) {
            this.setData({
                shopInfo: {
                    id: options.id
                }
            })
        }
    },
    init() {
        this.setData({
            page: 1,
            hasmore: true,
            shopList: []
        })
        this.getShopList()
    },
    daohang(e) {
        if (e.mark.gps) {
            let gps = e.mark.gps.split(',');
            wx.openLocation({
                latitude: Number(gps[1]), // 目标地点的纬度
                longitude: Number(gps[0]), // 目标地点的经度
                name: this.data.shopInfo.shopName,
                address: this.data.shopInfo.shopAddress,
                scale: 18, // 地图缩放级别
                success(res) {
                    // 打开成功的回调
                    console.log('打开成功', res);
                },
                fail(err) {
                    // 打开失败的回调
                    console.error('打开失败', err);
                }
            });
        }
    },
    tabnav(e) {
        console.log(e)
        var item = e.currentTarget.dataset.id
        this.setData({
            activeFlag: item
        })
        this.getShopList()
    },
    allShop() {
        this.setData({
            activeFlag: '',
        })
        this.getShopList()
    },
    getGPS() {
        let vm = this;
        wx.getLocation({
            type: 'wgs84', // 返回坐标类型，可选值：'wgs84'、'gcj02'
            success: function (res) {
                var latitude = res.latitude; // 纬度
                var longitude = res.longitude; // 经度
                var speed = res.speed; // 速度，单位 m/s
                var accuracy = res.accuracy; // 精度，单位 m
                Haversine({
                    lat: latitude,
                    lon: longitude,
                    ...app.getCunId()
                }).then(r => {
                    if (r.code == 0) {
                        vm.setData({
                            shopInfo: r.data,
                            zuijinId: r.data.id
                        })
                        vm.init();
                    }

                })
                console.log('Latitude: ' + latitude);
                console.log('Longitude: ' + longitude);
                console.log('Speed: ' + speed);
                console.log('Accuracy: ' + accuracy);

            },
            fail: function (err) {
                wx.navigateTo({
                    url: './list',
                })
                console.error('Failed to get location:', err);
            }
        });
    },
    //   getshopData:function(){
    //     var that = this
    //     getshop().then(res=>{
    //       var arr = res.data.cates
    //       var arr1 = []
    //       var dic = {name:'全部',id:''}
    //       arr1.push(dic)
    //       that.setData({
    //         shopData: arr1.concat(arr),
    //         myScore:res.data.myScore
    //       })
    //     })
    //   },
    syncInputValue(event) {
        // console.log(event)
        this.setData({
            kw: event.detail.value,
            page: 1,
            hasmore: true,
            shopList: []
        })
        this.getShopList();
    },
    getShopList() {
        wx.showLoading({
            title: '加载中...',
        })
        var that = this
        var dic = { ...app.getCunId() }
        dic.pageNo = that.data.page;
        dic.pageSize = 20;
        dic.kw = that.data.kw || '';
        dic.id = that.data.shopInfo.id;
        // dic.cate = that.data.activeFlag
        console.log(dic)
        // getShopList(dic).then(res => {
        shopdata(dic).then(res => {
            wx.hideLoading({
                success: (res) => { },
            })
            wx.stopPullDownRefresh({
                success: (res) => { },
            })
            var arr = res.data.szShopGoodResultPage.records
            if (arr.length < 20) {
                that.setData({
                    hasmore: false
                })
            }
            that.setData({
                shopInfo: res.data,
                shopList: that.data.shopList.concat(arr),
            })
        })
    },
    lookDetail: function (e) {
        var id = e.currentTarget.dataset.id
        wx.navigateTo({
            url: '../integral/detail?id=' + id,
        })
    },
    gorecord: function () {

        wx.navigateTo({
            url: '../../mine/exchange',
        })

    },

    changeTabs: function (e) {
        console.log(e)
        var index = e.detail.currentIndex
        var obj = this.data.shopData[index]
        this.setData({
            activeFlag: obj.id,
            page: 1,
            hasmore: true,
            shopList: []
        })
        this.getShopList()
    },

    changeShop() {
        wx.navigateTo({
            url: './list',
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
        if (app.isWxLogin(3)) {
            if (this.data.shopList.length == 0) {
                if (this.data.shopInfo && this.data.shopInfo.id) {
                    this.init()
                } else {
                    this.getGPS()
                }

            }
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
        this.setData({
            page: 1,
            hasmore: true,
            shopList: [],
            shopData: []
        })
        this.getShopList()
        // this.getshopData()
    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {
        if (this.data.hasmore) {
            this.data.page++
            this.getShopList()
        }
    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {

    }
})