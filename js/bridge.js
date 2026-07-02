(function () {
  function resolveDescribe() {
    if (typeof chordtabber_js_ChordTabberJs !== "undefined" && chordtabber_js_ChordTabberJs.describe) {
      return chordtabber_js_ChordTabberJs.describe.bind(chordtabber_js_ChordTabberJs);
    }
    if (typeof com !== "undefined"
        && com.sixstringcitizen
        && com.sixstringcitizen.chordtabber
        && com.sixstringcitizen.chordtabber.js
        && com.sixstringcitizen.chordtabber.js.ChordTabberJs) {
      return com.sixstringcitizen.chordtabber.js.ChordTabberJs.describe.bind(
        com.sixstringcitizen.chordtabber.js.ChordTabberJs
      );
    }
    if (typeof ChordTabberJs !== "undefined" && ChordTabberJs.describe) {
      return ChordTabberJs.describe.bind(ChordTabberJs);
    }
    return null;
  }

  const describeFn = resolveDescribe();
  if (!describeFn) {
    return;
  }

  window.ChordTabberApi = {
    describe(chord) {
      const json = describeFn(chord);
      return JSON.parse(json);
    }
  };
})();
