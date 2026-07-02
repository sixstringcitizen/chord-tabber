(function () {
  function resolveDescribe() {
    // TeaVM's @JSExport attaches the exported method directly as a top-level
    // global function (window.describe), not under a namespaced class path.
    if (typeof describe === "function") {
      return describe;
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
