// otherPage/pages/villageAffairs/index.js
import { getCate } from './../../../api/sunshine'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    list: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log(options)
    this.getList();
  },
  getList() {
    getCate().then(r => {
      if (r.code == 0) {
        this.setData({
          list: r.data
        })
      }
    })
  },

  goListPage(e) {
    wx.navigateTo({
      url: '/otherPage/pages/villageAffairs/list?id=' + e.mark.item.id + '&name=' + e.mark.item.name,
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

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})