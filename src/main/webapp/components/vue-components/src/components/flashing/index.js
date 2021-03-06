import Flashing from './src/flashing.vue'

if (typeof window !== 'undefined' && window.Vue) {
  /* eslint-disable */
  Vue.component(Flashing.name, Flashing)
  new Vue({
    el: '#app',
    data: {
      words: [
        ["是谁 ??"],
        [
          "她是谁",
          "她是谁",
          "她是谁",
          "她是谁",
          "她是谁",
          "她是谁",
          "她是谁",
          "她是谁",
          "她是谁",
          "她是谁"
        ],
        ["我媳妇儿", "媳妇儿"],
        ["我是媳妇儿是一个"],
        [
          "美丽",
          "美丽",
          "美丽",
          "美丽",
          "美丽",
          "美丽",
          "美丽",
          "美丽",
          "美丽",
          "美丽"
        ],
        ["善良", "善良", "善良", "善良", "善良"],
        [
          "优雅",
          "优雅",
          "优雅",
          "优雅",
          "优雅",
          "优雅",
          "优雅",
          "优雅",
          "优雅",
          "优雅",
          "优雅",
          "优雅"
        ],
        [
          "脱俗",
          "脱俗",
          "脱俗",
          "抽屉",
          "脱俗",
          "脱俗",
          "脱俗",
          "脱俗",
          "脱俗",
          "脱俗",
          "脱俗",
          "脱俗",
          "脱俗",
          "脱俗",
          "脱俗"
        ],
        ["可爱", "可爱", "可爱", "可爱", "可爱", "可爱", "可爱", "可爱"],
        ["天真", "天真", "抽屉", "天真", "天真", "天真"],
        [
          "温柔",
          "温柔",
          "温柔",
          "温柔",
          "温柔",
          "温柔",
          "温柔",
          "温柔",
          "温柔"
        ],
        ["多才", "多才", "多才", "多才", "多才", "多才", "多才"],
        [
          "贤惠",
          "贤惠",
          "贤惠",
          "贤惠",
          "贤惠",
          "贤惠",
          "贤惠",
          "贤惠",
          "贤惠",
          "贤惠",
          "贤惠"
        ],
        ["好媳妇儿"],
        ["这么好的媳妇儿"],
        ["我要娶她"],
        ["要给她买小裙裙"],
        ["买好吃的"],
        ["亲爱的", "媳妇"],
        ["你这么的可爱"],
        ["漂亮"],
        ["我要送一你"],
        ["❀❀"]
      ]
    }
  })
}

export default Flashing
